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
      const interfaces = new Set();
      for (const method of config.proxyProfile.methods) {
        method.params.forEach(function(param) {
          interfaces.add(param.canonicalName);
        });
        if (method.returnType) {
          interfaces.add(method.returnType.canonicalName);
          // 返回类型存在泛型场景，一般不会很多层
          if (method.returnType.generic) {
            method.returnType.generic.forEach(item => interfaces.add(item.type));
          }
        }
      }
      console.log(this.astjson);
      const dep = findInterfaces(Array.from(interfaces), this.astjson);
      config.interfaces = dep.interfaces.map(canonicalName => {
        return this.astjson.classMap[canonicalName] || this.astjson.enumMap[canonicalName];
      });
      config.typeList = dep.types.map(canonicalName => {
        return {
          canonicalName,
          types: this.astjson.declareMap[canonicalName],
        };
      });

      if (!config.proxyProfile) {
        this.logger.info('[jar2proxy] proxy %s not found.', apiConfig.interfaceName);
        continue;
      }
      const content = nunjucks.renderString(this.getTemplateStr(config.tpl), config);
      const proxyfile = path.join(this.proxyDir, config.proxyName) + '.js';
      this.logger.info('[jar2proxy] write proxy %s to %s', apiConfig.interfaceName, proxyfile);
      fs.writeFileSync(proxyfile, content);

      // typescript
      const typeFile = path.join(this.proxyDir, config.proxyName) + '.d.ts';
      try {
        // console.log(config);
        const typings = nunjucks.renderString(this.getTemplateStr('proxy.ts.tpl'), config);
        fs.writeFileSync(typeFile, typings);
      } catch (err) {
        console.log(err);
      }
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
    }
    for (const className in this.astjson.enumMap) {
      const classfile = this.initClassPath(className);
      const enumAST = this.astjson.enumMap[className];
      this.logger.info('[jar2proxy] render enum %s %j', className, enumAST);
      const content = nunjucks.renderString(this.getTemplateStr('enum.js.tpl'), enumAST);
      fs.writeFileSync(classfile, content);
    }
  }

  initClassPath(className) {
    const args = className.split('.');
    args.unshift(this.proxyClassDir);
    args[args.length - 1] = args[args.length - 1] + '.js';
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

let dependencies = new Set();
let types = new Set();
// 遍历所有的类型，找到依赖
function findInterfaces(interfaces, profile) {
  dependencies = new Set();
  types = new Set();
  for (const item of interfaces) {
    getDependencies(item, profile);
  }
  return { interfaces: Array.from(dependencies), types: Array.from(types) };
}

// 查找类型中依赖的类型
function getDependencies(canonicalName, profile) {
  // 枚举
  if (profile.enumMap[canonicalName]) {
    return dependencies.add(canonicalName);
  }

  // 防止循环引用
  // fix http://gitlab.alipay-inc.com/chair/chair/issues/2906
  if (dependencies.has(canonicalName)) {
    return;
  }

  // 类型声明
  if (Array.isArray(profile.declareMap[canonicalName]) && !canonicalName.startsWith('java.util.')) {
    profile.declareMap[canonicalName].forEach(item => {
      getDependencies(item, profile);
    });
    types.add(canonicalName);
    return;
  }
  const classMap = profile.classMap[canonicalName];

  dependencies.add(canonicalName);
  // 递归读取泛型依赖
  const handleTypeInGeneric = generic => {
    if (!Array.isArray(generic)) {
      return;
    }

    generic.forEach(item => {
      if (item.generic) {
        handleTypeInGeneric(item.generic);
      }

      getDependencies(item.type, profile);
    });
  };

  if (!classMap || !classMap.canonicalName) {
    return null;
  }

  // 抽象类可能没用 fields
  Array.isArray(classMap.fields) && classMap.fields.forEach(field => {
    if (canonicalName === field.canonicalName) {
      return;
    }

    // 泛型处理
    if (field.generic) {
      handleTypeInGeneric(field.generic);
    }

    getDependencies(field.canonicalName, profile);
  });
}
