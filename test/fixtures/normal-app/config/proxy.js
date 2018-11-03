'use strict';

const path = require('path');

module.exports = {
  group: 'SOFA',
  directoryToJar: path.join(__dirname, '../libs'),
  mavenRepository: null,
  services: [
    {
      appName: 'sofa',
      responseTimeout: 100,
      api: {
        userFacade: {
          interfaceName: 'com.ali.jar2proxy.normal.facade.UserFacade',
          responseTimeout: 1001,
        },
        topicConcernFacade: {
          interfaceName: 'com.ali.jar2proxy.generic.facade.TopicConcernFacade',
        },
      },
      dependency: [
        {
          groupId: 'com.alibaba.jar2proxy.facade',
          artifactId: 'jar2proxy-facade',
          version: '1.0.0',
          directoryToJar: '',
        },
      ],
    },
  ],
};
