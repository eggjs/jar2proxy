#!/usr/bin/env node

'use strict';

const program = require('commander');
const Jar2proxy = require('../lib/jar2proxy');
const debug = require('debug')('jar2proxy:bin');

program
  .option('-b, --base [base]', 'the base directory of the project')
  .option('-t, --tpl [tpl]', 'path to template')
  .option('-c, --config [config]', 'appoint the proxy config file path')
  .allowUnknownOption()
  .parse(process.argv);

const opts = {
  baseDir: program.base,
  tpl: program.tpl,
  proxyConfigPath: program.config,
  isProduction: process.env.NODE_ENV === 'production',
};
debug('%j', opts);
const jar2proxy = new Jar2proxy(opts);
jar2proxy.run().then(() => {
  console.log('[jar2proxy] Generated completed.');
  console.log('[jar2proxy] You can see detail at %s/logs/jar2proxy-*.log', jar2proxy.config.baseDir);
  setTimeout(() => {
    process.exit();
  }, 1000);
});
