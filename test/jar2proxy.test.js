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

      [
        'proxy/topicConcernFacade.d.ts',
        'proxy/userFacade.d.ts',
        'proxy_class/com/ali/jar2proxy/generic/enums/TOPIC_TYPE.d.ts',
        'proxy_class/com/ali/jar2proxy/generic/model/Pair.d.ts',
      ].forEach(file => {
        const filepath = path.join(__dirname, 'fixtures/ts-app/app', file);
        assert(!fs.existsSync(filepath));
      });

      done();
    });
  });

  it('should generate ts definition files', function(done) {
    const libspath = path.join(__dirname, './fixtures/ts-app/libs');
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
      baseDir: 'ts-app',
    }, (err, rs) => {
      assert(!err);
      assert(rs.stdout.includes('[jar2proxy] Generated completed.'));

      [
        'proxy/topicConcernFacade.d.ts',
        'proxy/userFacade.d.ts',
        'proxy_class/com/ali/jar2proxy/generic/enums/TOPIC_TYPE.d.ts',
        'proxy_class/com/ali/jar2proxy/generic/model/Pair.d.ts',
      ].forEach(file => {
        const filepath = path.join(__dirname, 'fixtures/ts-app/app', file);
        assert(fs.existsSync(filepath));
      });

      const filepath = path.join(__dirname, 'fixtures/ts-app/app/proxy/topicConcernFacade.d.ts');
      let proxyContent = fs.readFileSync(filepath, 'utf8');
      proxyContent = proxyContent.split('\r\n').join('\n');
      assert(proxyContent.includes('import TOPIC_TYPE from \'../proxy_class/com/ali/jar2proxy/generic/enums/TOPIC_TYPE\';'));
      assert(proxyContent.includes(`unpopOneTopic(
    request: TTopicPopRequest,
  ): Promise<CommunityCommonResult>;`));
      assert(proxyContent.includes(`declare module 'egg' {
  export interface IProxy {
    topicConcernFacade: TopicConcernFacade;
  }
}`));
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
