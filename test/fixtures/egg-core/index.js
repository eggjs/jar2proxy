'use strict';

const path = require('path');

exports.EggLoader = class EggLoader {
  get allPlugins() {
    return {
      proxyDemo: {
        enable: true,
        package: 'egg-proxy-demo',
        name: 'proxyDemo',
        path: path.join(__dirname, '../egg-proxy-demo'),
        version: '2.1.0',
      },
    };
  }

  loadPlugin() {}
};
