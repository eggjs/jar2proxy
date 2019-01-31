'use strict';

const { spawnSync } = require('child_process');
const fs = require('fs');
const { join } = require('path');
const os = require('os');

before(() => {
  // prepare all jar for test
  const basedir = join(__dirname, '../fixtures');
  // find all java project then build *.jar & *-sources.jar
  // java project should exist build.xml
  const dirs = fs.readdirSync(basedir);
  dirs.forEach(dir => {
    const build = join(basedir, dir, 'build.xml');
    if (!fs.existsSync(build)) {
      return;
    }
    const result = spawnSync(
      join(__dirname, `../../node_modules/mirant/bin/${os.platform() === 'win32' ? 'ant.bat' : 'ant'}`),
      [ '-f', build ]
    );
    if (result.error) {
      throw result.error;
    }
    if (!result.stdout.toString().includes('BUILD SUCCESSFUL')) {
      console.log(result.stdout.toString());
      console.log(result.stderr.toString());
      process.exit();
    }
  });

});
