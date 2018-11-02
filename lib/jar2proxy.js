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

const DEFAULT_API_OTPS = {
  tpl: 'proxy.js.tpl',
  group: 'HSF',
  version: '1.0',
  port: 12200,
  responseTimeout: 3000,
};

class Jar2proxy {

  constructor(options) {
    const {
      proxyConfigPath = 'config/proxy.js', // config/proxy.js
      baseDir = process.cwd(), // default value
      defaultTpl = 'proxy.js.tpl', // proxy.js.tpl
      isProduction = false,
    } = options;
    this.config = {
      baseDir,
      proxyConfigPath,
      defaultTpl,
      isProduction,
    };
    this.logger = require('./logger')(this.config.baseDir);
    debug('constructor %j', this.config);
    assert(fs.existsSync(baseDir), `Application baseDir not exists! ${baseDir}`);
    const configPath = path.join(baseDir, proxyConfigPath);
    assert(fs.existsSync(configPath), `Config file config/proxy.js not found! ${configPath}`);
    this.config.proxyConfigPath = configPath;
    this.jarDir = path.join(os.tmpdir(), Date.now().toString());
  }

  async run() {
    try {
      const { baseDir, proxyConfigPath } = this.config;
      const proxyConfig = new ProxyConfig({
        baseDir,
        proxyConfigPath,
        logger: this.logger,
      });
      const config = proxyConfig.readConfig();
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
    for (const appService of this.proxyConfig.services) {
      for (const apiName in appService.api) {
        const apiConfig = appService.api[apiName];
        this.logger.info('[jar2proxy] render proxy %s: %s', apiName, apiConfig.interfaceName);
        const proxyAST = this.astjson.proxyMap[apiConfig.interfaceName];
        if (!proxyAST) {
          this.logger.info('[jar2proxy] proxy %s not found.', apiConfig.interfaceName);
          continue;
        }
        const proxyName = apiName.substring(0, 1).toLowerCase() + apiName.substring(1);
        const proxyModel = {
          ...DEFAULT_API_OTPS,
          ...appService,
          ...apiConfig,
          proxyName,
          proxyProfile: proxyAST,
        };
        this.logger.info('[jar2proxy] render proxy with model: %j', proxyModel);
        const content = nunjucks.renderString(this.getTemplateStr(proxyModel.tpl), proxyModel);
        const proxyfile = path.join(this.proxyDir, proxyName) + '.js';
        this.logger.info('[jar2proxy] write proxy name to %s', proxyfile);
        fs.writeFileSync(proxyfile, content);
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
    args[ args.length - 1 ] = args[ args.length - 1 ] + '.js';
    const classfile = path.join.apply(null, args);
    mkdirp.sync(path.dirname(classfile));
    return classfile;
  }

  getTemplateStr(name) {
    const str = fs.readFileSync(path.join(__dirname, './tpls', name), 'utf8');
    // this.logger.info('[jar2proxy] get template %s', str);
    return str;
  }

}

module.exports = Jar2proxy;
