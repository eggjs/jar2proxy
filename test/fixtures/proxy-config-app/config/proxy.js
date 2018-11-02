'use strict';

module.exports = {
  services: [
    {
      appName: 'app-a1',
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
      api: {
        facadeNameB1: 'com.*.*.FacadeNameB1',
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
