'use strict';

const { execSync, spawnSync } = require('child_process');
const platform = require('./platform');
const { javahome, toolsPath } = require('./javahome');
const { join, delimiter, basename } = require('path');
const fs = require('fs');
// const isUTF8 = require('is-utf8');
const debug = require('debug')('jar2proxy:jar');

class JarProcessor {

  constructor(options) {
    this.logger = options.logger;
  }

  copyTo(jarFile, distdir) {
    fs.copyFileSync(jarFile, join(distdir, basename(jarFile)));
  }

  /**
   * extract all jar files to target dir
   * @param  {Array} jarFiles all fullpath jar
   * @param  {string} distdir target dir
   */
  extract(jarFiles, distdir) {
    debug('extract javahome: %s', javahome);
    const winprefix = platform === 'win' && /^\w:/.test(distdir) ? distdir.substring(0, 2) + ' && ' : '';
    const binjar = join(javahome, 'bin/jar');
    for (const jarFile of jarFiles) {
      if (!fs.existsSync(jarFile)) {
        console.log('[jar2proxy:extract] jar file not exists: %s.', jarFile);
        continue;
      }
      // const cmd = `unzip ${jarFile} -d ${distdir}`;
      // #37 兼容 windows 跨盘符 cd, d:/tmp
      const cmd = `${winprefix} cd ${distdir} && "${binjar}" -xf ${jarFile}`;
      debug('exec: %s', cmd);
      execSync(cmd, { encoding: 'utf8' });
    }
  }

  /**
   * @param  {String} sourcesDir sourcesDir
   */
  transferGBK2UTF8(/* sourcesDir */) {
    // const arrays = globby.sync([ '**/*.java' ], {
    //   cwd: sourcesDir,
    // });
    // for (const file of arrays) {
    //   const filePath = join(sourcesDir, file);
    //   const buf = fs.readFileSync(filePath);
    //   if (!isUTF8(buf)) {
    //     // transferGBK2UTF8
    //   }
    // }
  }

  /**
   * parse
   * @param  {String} sourcesDir source code dir
   * @param  {Object} proxyConfig proxy config
   * @return {Object} ast
   */
  parse(sourcesDir, proxyConfig) {
    const astfile = join(sourcesDir, 'proxy-ast.json');
    const classpath = [];
    this.logger.info('[jar2proxy] javahome: %s', javahome);
    this.logger.info('[jar2proxy] toolsPath: %s', toolsPath);
    classpath.push(toolsPath);
    classpath.push(join(__dirname, '../../bin/libs/fastjson-1.2.48.jar'));
    classpath.push(join(__dirname, '../../bin/libs/log4j-core-2.11.1.jar'));
    classpath.push(join(__dirname, '../../bin/libs/log4j-api-2.11.1.jar'));
    classpath.push(join(__dirname, '../../bin/libs/astparser.jar'));

    const cmd = [];
    // -Duser.language=en -Duser.country=US
    // Running the container sometimes initializes less than 256m of memory.
    // cmd.push('-Xms256m');
    cmd.push('-Xmx1024m');
    cmd.push('-Dfile.encoding=UTF-8');
    cmd.push('-Djava.awt.headless=true');
    cmd.push('-classpath');
    cmd.push(classpath.join(delimiter));
    cmd.push('com.ali.jar2proxy.astparser.AstParser');
    cmd.push('-source');
    cmd.push(sourcesDir);
    cmd.push('-output');
    cmd.push(astfile);

    const interfaceNames = [];
    proxyConfig.services.forEach(service => {
      for (const facadeName in service.api) {
        const facade = service.api[facadeName];
        const interfaceName = typeof facade === 'string' ? facade.split(':')[0] : facade.interfaceName;
        if (!interfaceName || interfaceName.indexOf('.') === -1) {
          throw new Error(`Please config interface name [${facadeName}:${interfaceName}] at proxy.js with right package name`);
        }
        interfaceNames.push(interfaceName);
      }
    });
    cmd.push('-proxy');
    cmd.push(interfaceNames.join(':'));

    const bin = join(javahome, 'bin/java');
    this.logger.info('[jar2proxy] parse: %s %s', bin, cmd.join(' '));
    const result = spawnSync(bin, cmd, {
      // cwd: sourcesDir,
      stdio: 'pipe',
    });
    this.logger.info('%s', result.stdout);
    this.logger.info('%s', result.stderr);
    return astfile;
  }
}

module.exports = JarProcessor;
