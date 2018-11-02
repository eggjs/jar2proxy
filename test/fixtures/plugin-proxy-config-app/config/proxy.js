'use strict';

module.exports = {
  // responseTimeout config in plugin, app plugin extends from plugin config
  services: [
    {
      appName: 'app-a1',
      api: {
        facadeName: 'com.a.x.FacadeName',
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
      appName: 'appname-conflict', // conflict in plugin proxyDemo
      api: {
        conflictFacadeName: 'com.a.x.ConflictFacadeName',
      },
      dependency: [
        {
          groupId: 'group-conflict',
          artifactId: 'artifact-conflict',
          version: '1.0.0',
        },
        {
          groupId: 'group-repeat-in-app',
          artifactId: 'artifact-repeat',
          version: '1.1.0',
        },
      ],
    },
  ],
};
