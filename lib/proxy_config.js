'use strict';

// const assert = require('assert');
const path = require('path');
const fs = require('fs');
const getPlugins = require('egg-utils').getPlugins;
const assert = require('assert');

class ProxyConfig {

  constructor(options) {
    const { baseDir, proxyConfigPath, logger } = options;
    assert(logger, 'property logger can\'t be empty');
    assert(baseDir, 'property baseDir can\'t be empty');
    assert(proxyConfigPath, 'property proxyConfigPath can\'t be empty');
    this.baseDir = baseDir;
    this.proxyConfigPath = proxyConfigPath;
    this.logger = logger;
  }

  readConfig() {
    const proxyConfig = require(this.proxyConfigPath);
    const { eggFramework = 'egg' } = proxyConfig;
    this.logger.info('getPlugins %s %s', this.baseDir, eggFramework);
    let plugins;
    try {
      plugins = getPlugins({
        baseDir: this.baseDir,
        framework: path.join(this.baseDir, 'node_modules', eggFramework),
      });
    } catch (err) {
      plugins = {};
      this.logger.info('getPlugins failed.');
      this.logger.error(err);
    }
    this.logger.info('getPlugins: %j', plugins);
    this.appProxyConfig = proxyConfig;
    this.pluginProxyConfigs = this.readPluginProxyConfig(plugins);
    this.proxyConfig = this.mergeProxyConfig();
    return this.proxyConfig;
  }

  readPluginProxyConfig(plugins) {
    // "view": {
    //   "enable": true,
    //   "package": "egg-view",
    //   "name": "view",
    //   "path": "~/jar2proxy/test/fixtures/plugin-app/node_modules/egg-view",
    //   "version": "2.1.0"
    // }
    const proxyConfigs = [];
    for (const pluginName in plugins) {
      const plugin = plugins[pluginName];
      if (!plugin.enable) {
        continue;
      }
      const proxyPath = path.join(plugin.path, 'app/proxy');
      const configPath = path.join(plugin.path, 'config/proxy.js');
      // If plugin author pushed jar2proxy result files, jar2proxy will skip process when exec in application
      if (!fs.existsSync(proxyPath) && fs.existsSync(configPath)) {
        const pluginConfig = require(configPath);
        pluginConfig.__fromPlugin = plugin.path;
        proxyConfigs.push(pluginConfig);
      }
    }
    return proxyConfigs;
  }

  // merge all plugin proxy config to app/config/proxy.js
  mergeProxyConfig() {
    // app/config/proxy.js
    let appConfig = this.appProxyConfig;
    // ${plugins}/config/proxy.js
    this.pluginProxyConfigs.forEach(pluginConfig => {
      appConfig = this.mergePluginConfigToAppConfig(this.appProxyConfig, pluginConfig);
    });
    if (!Array.isArray(appConfig.services)) {
      throw new Error('proxyConfig.services can\'t be empty.');
    }
    const dependencies = this.mergeDependencies(appConfig);
    return {
      ...appConfig,
      dependencies,
    };
  }

  mergePluginConfigToAppConfig(appConfig, pluginConfig) {
    const mergedConfig = Object.assign({}, appConfig);
    const keys = [ 'group', 'responseTimeout', 'errorAsNull' ];
    function mergeService(pluginService) {
      // 打标签，方便后面合并 jar 处理
      pluginService.dependency = Array.isArray(pluginService.dependency) ? pluginService.dependency : [ pluginService.dependency ];
      pluginService.dependency.forEach(item => {
        if (!item) {
          return;
        }
        item.__fromPlugin = pluginConfig.__fromPlugin;
      });
      const targetService = mergedConfig.services.find(mergedService => {
        return mergedService.appName === pluginService.appName;
      });
      if (targetService) {
        targetService.api = { ...targetService.api, ...pluginService.api };
        const diffDependency = pluginService.dependency.filter(function(pluginDepd) {
          if (!pluginDepd) {
            return false;
          }
          const isSameDep = targetService.dependency.some(function(appDepd) {
            return pluginDepd.groupId === appDepd.groupId && pluginDepd.artifactId === appDepd.artifactId && pluginDepd.version === appDepd.version;
          });
          return !isSameDep;
        });
        targetService.dependency = targetService.dependency.concat(diffDependency);
      } else {
        mergedConfig.services.push(pluginService);
      }
    }
    for (const keyName in pluginConfig) {
      if (keys.some(item => item === keyName) && !mergedConfig.hasOwnProperty(keyName)) {
        mergedConfig[keyName] = pluginConfig[keyName];
        console.log('[jar2proxy] `%s` not found in config/proxy.js use plugin config first [%s]', keyName, mergedConfig[keyName]);
      }
      if (keyName !== 'services') {
        continue;
      }
      pluginConfig.services.forEach(mergeService);
    }
    return mergedConfig;
  }

  mergeDependencies(appConfig) {
    const tmpAppDependencies = [];
    const appDependencies = [];
    const pluginDependencies = [];
    const services = appConfig.services;
    for (let i = 0; i < services.length; i++) {
      const service = services[i];
      const depds = Array.isArray(service.dependency) ? service.dependency : [ service.dependency ];
      depds.forEach(depd => {
        if (!depd) {
          return;
        }
        if (depd.__fromPlugin) {
          pluginDependencies.push(depd);
        } else {
          tmpAppDependencies.push(depd);
        }
      });
    }
    // check if app config had conflict depd config
    for (let j = 0; j < tmpAppDependencies.length; j++) {
      const depd = tmpAppDependencies[j];
      if (appDependencies.find(item => item.groupId === depd.groupId && item.artifactId === depd.artifactId)) {
        continue;
      }
      const result = tmpAppDependencies.filter(item => {
        return item.artifactId === depd.artifactId && item.groupId === depd.groupId;
      });
      if (result.length >= 2) {
        const message = `
        App dependency "groupId:${depd.groupId}, artifactId:${depd.artifactId}" appeared twice, you should delete the old one!
        Problem dependencies: ${JSON.stringify(result, null, 2)}
        `;
        this.dependencyConflictCheck(result, message);
        appDependencies.push(result[0]);
      } else {
        appDependencies.push(depd);
      }
    }

    const pluginHasChecked = [];
    pluginDependencies.forEach(pluginDepd => {
      if (!pluginDepd) {
        return;
      }
      if (pluginHasChecked.find(item => item.groupId === pluginDepd.groupId && item.artifactId === pluginDepd.artifactId)) {
        return;
      }
      const result = pluginDependencies.filter(function(item) {
        if (!item || !pluginDepd) {
          return false;
        }
        return item.artifactId === pluginDepd.artifactId && item.groupId === pluginDepd.groupId;
      });
      const existInApp = appDependencies.find(appDepd => appDepd.artifactId === pluginDepd.artifactId && appDepd.groupId === pluginDepd.groupId);
      if (result.length >= 2 && !existInApp) {
        const message = `
          Plugin dependency "groupId:${pluginDepd.groupId}, artifactId:${pluginDepd.artifactId}" appeared twice but not found in app, you can override plugin config with assign newer one in app!
          Problem dependencies: ${JSON.stringify(result, null, 2)}
        `;
        this.dependencyConflictCheck(result, message);
        appDependencies.push(result[0]);
      } else if (result.length === 1) {
        if (existInApp) {
          this.logger.info('[jar2proxy] App and Plugin dependency conflict, default use app dependency first!');
          this.logger.info(`Plugin dependency: ${JSON.stringify(pluginDepd, null, 2)}`);
          this.logger.info(`App dependency: ${JSON.stringify(existInApp, null, 2)}`);
        } else {
          appDependencies.push(pluginDepd);
        }
      }
      pluginHasChecked.push(pluginDepd);
    });
    return appDependencies;
  }

  dependencyConflictCheck(dependencies, message) {
    if (dependencies.length === 1) {
      return;
    }
    const versions = [];
    dependencies.forEach(depd => {
      if (!versions.some(ver => ver === depd.version)) {
        versions.push(depd.version);
      }
    });
    if (versions.length === 1) {
      // print the same depd, but do not stop task
      console.warn(message);
    } else {
      // multiple versions, break the task.
      throw new Error(message);
    }
  }

}

module.exports = ProxyConfig;
