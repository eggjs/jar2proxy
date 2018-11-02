'use strict';

const os = require('os');

const _ = os.platform();
let platform;
switch (_) {
  case 'darwin':
    platform = 'osx';
    break;
  case 'linux':
  case 'freebsd':
    platform = 'linux';
    break;
  case 'win32':
  case 'cygwin':
    platform = 'win';
    break;
  default:
    break;
}

module.exports = platform;
