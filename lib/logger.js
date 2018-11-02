'use strict';

const path = require('path');
const Logger = require('mini-logger');
const mkdirp = require('mkdirp');

/**
 * log all runtime info to jar2proxy-info.log
 * @param {string} baseDir application dir
 * @return {Logger} return logger instance
 */
module.exports = baseDir => {
  const logdir = path.join(baseDir, 'logs');
  mkdirp.sync(logdir);
  return Logger({
    dir: logdir,
    // mkdirp: true,
    categories: [ 'info' ],
    format: '[jar2proxy-{category}.]YYYY-MM-DD[.log]',
    flushInterval: '1ms',
    timestamp: true,
    seperator: '\n',
  });
};
