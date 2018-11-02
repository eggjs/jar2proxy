'use strict';

const path = require('path');
const fs = require('fs');
const coffee = require('coffee');
const process = require('child_process');
const rimraf = require('rimraf');
const assert = require('assert');

before(function() {

  this.setBaseDir = file => {
    this.baseDir = path.join(__dirname, 'fixtures/', file);
  };

  this.wait = time => new Promise(resolve => {
    setTimeout(resolve, time);
  });

  this.prepareApp = baseDir => {
    try {
      const logPath = path.join(baseDir, 'logs');
      const modulePath = path.join(baseDir, 'node_modules');
      rimraf.sync(logPath);
      rimraf.sync(modulePath);
      assert(!fs.existsSync(logPath));
      assert(!fs.existsSync(modulePath));
    } catch (err) {
      console.log(err.stack);
    }
    console.log('start install modules.');
    process.execSync('npm install', {
      cwd: baseDir,
    });
  };

  this.jar2proxy = (options, callback) => {
    if (options.baseDir) {
      this.setBaseDir(options.baseDir);
    }
    rimraf.sync(path.join(this.baseDir, './proxy'));
    rimraf.sync(path.join(this.baseDir, './proxy_class'));
    this.prepareApp(this.baseDir);
    let args = [
      path.join(__dirname, '../bin/jar2proxy.js'),
      '--base', this.baseDir,
    ];
    const opts = {};
    if (options.config) {
      args = args.concat([ '--config', path.join(this.baseDir, options.config) ]);
    }
    if (options.tpl) {
      args = args.concat([ '--tpl', path.join(this.baseDir, options.tpl) ]);
    }

    const c = coffee.spawn('node', args, opts).debug();
    c.end((err, ps) => {
      // ps: {stdout, stderr, code, error}
      const classMapPath = path.join(this.baseDir, './app/proxy/class_map.js');
      if (fs.existsSync(classMapPath)) {
        let classMapStr = '';
        const proxyClassDir = path.join(this.baseDir, './app/proxy/');
        const readFiles = function(dir) {
          const files = fs.readdirSync(dir);
          files.forEach(item => {
            if (/\.js$/.test(item)) {
              const str = fs.readFileSync(path.join(dir, item), 'utf8');
              classMapStr += str.substring(str.indexOf('//^') + 3, str.indexOf('//$'));
              return;
            }
            const subDir = path.join(dir, item);
            const stat = fs.statSync(subDir);
            if (stat.isDirectory()) {
              readFiles(subDir);
            }
          });
        };
        readFiles(proxyClassDir);
        this.proxyClassMap = classMapStr;
        this.classMap = require(classMapPath);
      } else {
        this.proxyClassMap = null;
      }
      this.stdout = c.stdout;
      this.stderr = c.stderr;
      callback(err, ps);
    });
  };

});
