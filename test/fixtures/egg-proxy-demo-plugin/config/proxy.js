'use strict';

module.exports = {
  responseTimeout: 5000,
  services: [
    {
      appName: 'plugin-a1',
      api: {
        pluginFacadeName: 'com.*.*.PluginFacadeName',
      },
      dependency: [
        {
          groupId: 'plugin-group-a1',
          artifactId: 'artifact-a1',
          version: '1.0.0',
        },
      ],
    },
    {
      appName: 'appname-conflict',
      api: {
        conflictFacadeName2: 'com.a.x.ConflictFacadeName2',
      },
      dependency: [
        {
          groupId: 'group-conflict2',
          artifactId: 'artifact-conflict2',
          version: '1.0.0',
        },
        {
          groupId: 'group-repeat-in-plugin',
          artifactId: 'artifact-repeat',
          version: '1.0.0',
        },
        {
          groupId: 'group-repeat-in-plugin',
          artifactId: 'artifact-repeat',
          version: '1.0.0',
        },
        {
          groupId: 'group-repeat-in-app',
          artifactId: 'artifact-repeat',
          version: '1.0.0',
        },
      ],
    },
  ],
};
