'use strict';

const platform = require('./platform');
const { join } = require('path');
const child_process = require('child_process');
const fs = require('fs');
const debug = require('debug')('jar2proxy:javahome');

function exec(cmd) {
  return (child_process.execSync(cmd) || '').toString().replace('\n', '');
}

function osx() {
  let toolsPath = '';
  let javahome = (process.env.JAVA_HOME || '').trim();
  if (!javahome) {
    javahome = exec('/usr/libexec/java_home');
    debug('osx.libexec', javahome);
  }

  if (javahome) {
    if (fs.existsSync(javahome)) {
      javahome = fs.realpathSync(javahome);
      debug('osx.realpath', javahome);
    }

    // only support jdk1.8
    toolsPath = join(javahome, 'lib/tools.jar');

    if (!fs.existsSync(toolsPath)) {
      throw Error('lib/tools.jar not found in JAVA_HOME! Only support jdk1.8, javahome: ' + javahome);
    }
  }

  return {
    javahome,
    toolsPath,
  };
}

function linux() {
  let toolsPath = '';
  let javahome = (process.env.JAVA_HOME || '').trim();
  if (fs.existsSync(javahome)) {
    javahome = fs.realpathSync(javahome);
  }
  toolsPath = join(javahome, 'lib/tools.jar');
  return {
    javahome,
    toolsPath,
  };
}

function win() {
  let toolsPath = '';
  let javahome = (process.env.JAVA_HOME || '').trim();
  if (javahome) {
    javahome = javahome.replace(/[\r\n]/g, '');
    toolsPath = join(javahome, 'lib/tools.jar');
  }
  // try find jdk files
  if (!fs.existsSync(toolsPath)) {
    const roots = [ 'C:/Program Files/Java', 'C:/Program Files (x86)/Java' ];
    for (let r = 0; r < roots.length; r++) {
      const root = roots[r];
      if (fs.existsSync(root)) {
        const files = fs.readdirSync(root);
        for (let i = 0, len = files.length; i < len; i++) {
          const file = files[i];
          if (/^jdk/.test(file)) {
            javahome = join(root, file);
            toolsPath = join(root, file, 'lib/tools.jar');
            break;
          }
        }
      }
    }
  }
  return {
    javahome,
    toolsPath,
  };
}

let javahome;
if (platform === 'osx') {
  javahome = osx();
} else if (platform === 'linux') {
  javahome = linux();
} else if (platform === 'win') {
  javahome = win();
}
if (!javahome || !fs.existsSync(javahome.toolsPath)) {
  throw new Error((javahome.toolsPath || 'JAVA_HOME') + ' Can`t be found or not correct!');
}

module.exports = javahome;
