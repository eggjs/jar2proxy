'use strict';

require('./global');
const fs = require('fs');
const path = require('path');
const mkdirp = require('mkdirp');
const assert = require('assert');

describe('test/jar2proxy.test.js', function() {
  it('jar2proxy bin', function(done) {
    const libspath = path.join(__dirname, './fixtures/normal-app/libs');
    mkdirp.sync(libspath);
    fs.copyFileSync(
      path.join(__dirname, './fixtures/facade/target/facade.jar'),
      path.join(libspath, 'jar2proxy-facade-1.0.0.jar')
    );
    fs.copyFileSync(
      path.join(__dirname, './fixtures/facade/target/facade-sources.jar'),
      path.join(libspath, 'jar2proxy-facade-1.0.0-sources.jar')
    );
    this.jar2proxy({
      baseDir: 'normal-app',
    }, (err, rs) => {
      assert(!err);
      assert(rs.stdout.includes('[jar2proxy] Generated completed.'));
      done();
    });
  });

  it.skip('plugin', function(done) {
    this.jar2proxy({
      baseDir: 'plugin-app',
    }, (err, rs) => {
      console.log(err, rs);
      done();
    });
  });
});
