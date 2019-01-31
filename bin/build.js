'use strict';

/*
 * Compatible with windows operating environment
 */
const path = require('path');
const { spawnSync } = require('child_process');
const os = require('os');

const res = spawnSync(
  path.join(__dirname, `../node_modules/mirant/bin/${os.platform() === 'win32' ? 'ant.bat' : 'ant'}`),
  [ '-f', path.join(__dirname, '../astparser/build.xml') ]
);

console.log(res.error || '');
console.log(res.stderr && res.stderr.toString());
console.log(res.stdout && res.stdout.toString());
