'use strict';

const path = require('path');
const os = require('os');
const fs = require('fs');
const assert = require('assert');
const mkdirp = require('mkdirp');
const JarProcessor = require('../lib/java/jar');
const debug = require('debug')('jar2proxy:jar2proxy');
const ProxyConfig = require('./proxy_config');
const Dependency = require('./maven/dependency');
const globby = require('globby');
const nunjucks = require('./nunjucks');
const ProfileHelper = require('./profile_helper');

class Jar2proxy {

  constructor(options) {
    const {
      baseDir = process.cwd(), // default value
      isProduction = false,
      proxyConfigPath = 'config/proxy.js', // config/proxy.js
    } = options;
    this.options = options;
    this.config = {
      baseDir,
      isProduction,
      proxyConfigPath,
    };
    this.logger = require('./logger')(this.config.baseDir);
    debug('constructor %j', this.config);
    assert(fs.existsSync(baseDir), `Application baseDir not exists! ${baseDir}`);
    const configPath = path.join(baseDir, proxyConfigPath);
    assert(fs.existsSync(configPath), `Config file config/proxy.js not found! ${configPath}`);
    this.config.proxyConfigPath = configPath;
    this.jarDir = path.join(os.tmpdir(), Date.now().toString());
    this.profileHelper = null;
  }

  loadConfig() {
    const { baseDir, proxyConfigPath } = this.config;
    const proxyConfig = new ProxyConfig({
      tpl: this.options.tpl,
      baseDir,
      proxyConfigPath,
      logger: this.logger,
    });
    const config = proxyConfig.readConfig();
    return config;
  }

  async run() {
    try {
      const { baseDir } = this.config;
      const config = this.loadConfig();
      this.logger.info('%j', config);
      this.proxyConfig = config;
      const depd = new Dependency({
        baseDir,
        directoryToJar: config.directoryToJar,
        mavenRepository: config.mavenRepository,
        dependencies: config.dependencies,
        logger: this.logger,
        jarDir: this.jarDir,
      });
      await depd.download();
      await this.genAST(config);
      await this.renderByAST();
    } catch (err) {
      this.logger.error(err);
      this.logger.flush();
      console.log(err.stack);
    }
  }

  async genAST(proxyConfig) {
    const jar = new JarProcessor({
      logger: this.logger,
    });
    const arr = globby.sync([ '*-sources.jar' ], {
      cwd: this.jarDir,
    });
    jar.extract(arr.map(jarName => path.join(this.jarDir, jarName)), this.jarDir);
    const astfile = jar.parse(this.jarDir, proxyConfig);
    this.logger.info('[jar2proxy] astfile: %s', astfile);
    if (!fs.existsSync(astfile)) {
      throw new Error('Ast result file can`t be found');
    }
    this.astjson = require(astfile);
    this.profileHelper = new ProfileHelper(this.astjson);
  }

  async renderByAST() {
    this.proxyDir = path.join(this.config.baseDir, 'app/proxy');
    this.proxyClassDir = path.join(this.config.baseDir, 'app/proxy_class');
    mkdirp.sync(this.proxyDir);
    mkdirp.sync(this.proxyClassDir);
    fs.copyFileSync(
      path.join(__dirname, './tpls/proxy_class.js'),
      path.join(this.proxyClassDir, './index.js')
    );
    this.renderProxy();
    this.renderClass();
  }

  renderProxy() {
    const { proxyList } = this.proxyConfig;
    for (const apiConfig of proxyList) {
      this.logger.info('[jar2proxy] render proxy with api config: %j', apiConfig);
      const config = {
        ...apiConfig,
        proxyProfile: this.astjson.proxyMap[apiConfig.interfaceName],
      };
      if (!config.proxyProfile) {
        this.logger.info('[jar2proxy] proxy %s not found.', apiConfig.interfaceName);
        continue;
      }

      if (this.proxyConfig.tsd) {
        const types = new Set();
        for (const method of config.proxyProfile.methods) {
          if (method.returnType) {
            types.add(method.returnType.canonicalName);
            // 返回类型存在泛型场景，一般不会很多层
            if (method.returnType.generic) {
              method.returnType.generic.forEach(item => types.add(item.type));
            }
          } else {
            const methodRaw = (method.raw || 'boolean').trim().split('\n');
            const returnText = methodRaw[methodRaw.length - 1];
            // 返回类型可能不存在，这里直接从 raw 中读取
            const type = returnText.split(' ')[0].trim();
            // 如果是大写字母开头，直接返回 any， java 复杂对象在字符串里面无法
            // 直接解析，这里返回泛型 T ，最终输出为 any ，不至于报错
            method.returnType = { canonicalName: type };
          }

          method.params = method.params || [];
          method.paramsStr = '';
          for (const param of method.params) {
            method.paramsStr += ',  ' + param.paramName;
            types.add(param.canonicalName);
          }
          method.commentText += '\n Java code: ' + (method.raw || '').replace(/\n\s*/g, '\n    ');
          method.commentText += '\n Note: You can get more information about the return java class type from proxy_class_map.js';
        }
        const dep = this.profileHelper.getDependencies(Array.from(types));
        config.proxyProfile.dependencies = dep.interfaces;

        const typeFile = path.join(this.proxyDir, config.proxyName) + '.d.ts';
        const typings = nunjucks.renderString(this.getTemplateStr('proxy.ts.tpl'), config);
        fs.writeFileSync(typeFile, typings);
      }

      const content = nunjucks.renderString(this.getTemplateStr(config.tpl), config);
      const proxyfile = path.join(this.proxyDir, config.proxyName) + '.js';
      this.logger.info('[jar2proxy] write proxy %s to %s', apiConfig.interfaceName, proxyfile);
      fs.writeFileSync(proxyfile, content);
    }
  }

  renderClass() {
    for (const className in this.astjson.classMap) {
      const classfile = this.initClassPath(className);
      const classAST = this.astjson.classMap[className];
      this.logger.info('[jar2proxy] render class %s %j', className, classAST);
      const content = nunjucks.renderString(this.getTemplateStr('class.js.tpl'), {
        class: classAST,
      });
      fs.writeFileSync(classfile, content);

      if (this.proxyConfig.tsd) {
        const dep = this.profileHelper.getDependencies([ className ]);
        // 排除自己
        const dependencies = dep.interfaces.filter(intf => intf.canonicalName !== className);

        const classTypingPath = this.initClassPath(className, '.d.ts');
        const tsContent = nunjucks.renderString(this.getTemplateStr('class.ts.tpl'), {
          dependencies,
          class: classAST,
        });
        fs.writeFileSync(classTypingPath, tsContent);
      }
    }
    for (const className in this.astjson.enumMap) {
      const classfile = this.initClassPath(className);
      const enumAST = this.astjson.enumMap[className];
      this.logger.info('[jar2proxy] render enum %s %j', className, enumAST);
      const content = nunjucks.renderString(this.getTemplateStr('enum.js.tpl'), enumAST);
      fs.writeFileSync(classfile, content);

      if (this.proxyConfig.tsd) {
        const enumTypingPath = this.initClassPath(className, '.d.ts');
        const arr = className.split('.');
        enumAST.enumName = arr[arr.length - 1];
        const tsContent = nunjucks.renderString(this.getTemplateStr('enum.ts.tpl'), enumAST);
        fs.writeFileSync(enumTypingPath, tsContent);
      }
    }
  }

  initClassPath(className, ext = '.js') {
    const args = className.split('.');
    args.unshift(this.proxyClassDir);
    args[args.length - 1] = args[args.length - 1] + ext;
    const classfile = path.join.apply(null, args);
    mkdirp.sync(path.dirname(classfile));
    return classfile;
  }

  getTemplateStr(name) {
    if (path.isAbsolute(name)) {
      return fs.readFileSync(name, 'utf8');
    }
    const str = fs.readFileSync(path.join(__dirname, './tpls', name), 'utf8');
    // this.logger.info('[jar2proxy] get template %s', str);
    return str;
  }

}

module.exports = Jar2proxy;
