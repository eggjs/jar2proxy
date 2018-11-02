'use strict';

const path = require('path');
const fs = require('fs');
const mkdirp = require('mkdirp');
const urllib = require('urllib');
const copy = require('copy-to');
const xml2map = require('xml2map');
// const rimraf = require('rimraf');

class Dependency {

  constructor(config) {
    this.config = config;
    const {
      // dependencies,
      baseDir,
      directoryToJar,
      // mavenRepository,
      logger,
      jarDir,
    } = this.config;
    // assert(dependencies.length > 0, '[jar2proxy] no dependencies config.');
    this.directoryToJar = directoryToJar || path.join(baseDir, 'assembly');
    this.jarDir = jarDir;
    mkdirp.sync(this.jarDir);
    this.logger = logger;
  }

  // _init() {
  //   mkdirp.sync(this.jarDir);
  //   rimraf.sync(path.join(this.jarDir, './*'));
  // }

  async download() {
    await this.execDownload();
  }

  async execDownload() {
    const jarUrls = {};
    const { mavenRepository, dependencies } = this.config;
    for (let i = 0; i < dependencies.length; i++) {
      const dependency = dependencies[i];
      if (!dependency) {
        continue;
      }
      const key = dependency.artifactId + '-' + dependency.version;
      const helper = {
        groupId: dependency.groupId,
        artifactId: dependency.artifactId,
        version: dependency.version,
        mavenRepository,
        timestamp: Date.now(),
        ignoreSources: dependency.ignoreSources,
      };
      // if mavenRepository is empty only try read file from local libs dir
      if (mavenRepository && /-SNAPSHOT$/i.test(helper.version)) {
        const metadataUrl = `${mavenRepository}/${helper.groupId.replace(/\./g, '/')}/${helper.artifactId}/${helper.version}/maven-metadata.xml`;
        this.logger.info('[jar2proxy] metadata %s', metadataUrl);
        const rvData = await this.request(metadataUrl, {
          method: 'GET',
          headers: {
            accept: '*/*',
            'accept-language': 'zh-CN,zh;',
          },
          timeout: 30000,
        }, 3);
        if (rvData.status === 200) {
          rvData.data = xml2map.tojson(rvData.data.toString());
        }
        this.logger.info('[jar2proxy] SNAPSHOT metadata: %j', rvData.data);
        if (!rvData.data.metadata || !rvData.data.metadata.versioning) {
          throw new Error(`Jar Not Found SNAPSHOT: ${helper.groupId}/${helper.artifactId}/${helper.version}`);
        }
        if (!rvData.data.metadata.versioning.snapshotVersions) {
          const snapshot = rvData.data.metadata.versioning.snapshot;
          const version = `${helper.version.replace('-SNAPSHOT', '')}-${snapshot.timestamp}-${snapshot.buildNumber}`;
          jarUrls[key + '.jar'] = copy({ jarVersion: version }).and(helper).to();
          if (!helper.ignoreSources) jarUrls[key + '-sources.jar'] = copy({ jarVersion: `${version}-sources` }).and(helper).to({ sources: true });
        } else {
          const versions = rvData.data.metadata.versioning.snapshotVersions.snapshotVersion;
          const sources = versions.filter(version => version.classifier === 'sources')[0];
          if (!sources) {
            throw new Error(`${key}-sources.jar is missing, Please contact the package administrator.`);
          }
          const jar = versions.filter(version => !version.classifier)[0];
          jarUrls[key + '.jar'] = copy({ jarVersion: sources.value }).and(helper).to();
          if (!helper.ignoreSources) jarUrls[key + '-sources.jar'] = copy({ jarVersion: `${jar.value}-sources` }).and(helper).to({ sources: true });
        }
      } else {
        jarUrls[key + '.jar'] = helper;
        if (!helper.ignoreSources) jarUrls[key + '-sources.jar'] = copy({ jarVersion: `${helper.version}-sources` }).and(helper).to({ sources: true });
      }
    }
    await Promise.all(Object.keys(jarUrls).map(fileName => this.createTask(jarUrls[fileName], fileName)));
  }

  async createTask(helper, fileName) {
    this.logger.info(this.jarDir, fileName);
    if (!helper.mavenRepository) {
      await this.createCopyTask(helper, fileName);
      return;
    }
    const startTime = Date.now();
    const filepath = path.join(this.jarDir, fileName);
    const jarUrl = `${helper.mavenRepository}/${helper.groupId.replace(/\./g, '/')}/${helper.artifactId}/${helper.version}/${helper.artifactId}-${helper.jarVersion ? helper.jarVersion : helper.version}.jar`;
    this.logger.info('downloading: %j %s', helper, jarUrl);
    const rvData = await this.request(jarUrl, {
      method: 'GET',
      headers: {
        accept: '*/*',
        'accept-language': 'zh-CN,zh;',
      },
      writeStream: fs.createWriteStream(filepath),
      // followRedirect: true,
      timeout: 600 * 1000,
    }, 3);
    if (rvData.status !== 200) {
      throw new Error('Jar Not Found ' + jarUrl);
    }
    this.logger.info('[jar2proxy] Downloaded %s %s %sms', jarUrl, rvData.status, Date.now() - startTime);
  }

  // try copy jar file from jarDir if mavenRepository empty
  async createCopyTask(helper, fileName) {
    const filepath = path.join(this.jarDir, fileName);
    fs.copyFileSync(path.join(this.directoryToJar, fileName), filepath);
  }

  async request(url, args, retry) {
    retry--;
    try {
      return await urllib.request(url, args);
    } catch (err) {
      if (retry <= 0) {
        throw err;
      }
      if (err.code === 'ENOTFOUND' || err.code === 'ECONNRESET' || err.code === 'ENETRESET') {
        console.warn('[jar2proxy] request %s error: %s, retry after 100ms, left: %s', url, err, retry);
        console.error(err);
        return await this.request(url, args, retry);
      }
      throw err;
    }
  }
}

module.exports = Dependency;
