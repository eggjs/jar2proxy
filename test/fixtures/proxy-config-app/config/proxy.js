'use strict';

const path = require('path');

module.exports = {
  group: 'DUBBO',
  tpl: path.join(__dirname, './proxy-demo.js.tpl'),
  services: [
    {
      appName: 'app-a1',
      version: 'version-a1',
      api: {
        facadeName: 'com.*.*.FacadeName',
      },
      dependency: [
        {
          groupId: 'group-a1',
          artifactId: 'artifact-a1',
          version: '1.0.0',
        },
        {
          groupId: 'group-a2',
          artifactId: 'artifact-a2',
          version: '1.0.0',
        },
      ],
    },
    {
      appName: 'app-b1',
      version: 'version-b1',
      api: {
        facadeNameB1: {
          interfaceName: 'com.*.*.FacadeNameB1',
          version: 'version-b1-1',
        },
      },
      dependency: [
        {
          groupId: 'group-b1',
          artifactId: 'artifact-b1',
          version: '1.0.0',
        },
        {
          groupId: 'group-a2',
          artifactId: 'artifact-a2',
          version: '1.0.0',
        },
      ],
    },
  ],
};
