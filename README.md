# jar2proxy

Developer tool for egg-sofa-rpc, You can easily use this tool to convert Java interface definitions into egg executable code.

---

# Environmental requirements

- jdk 1.8
- node >= 8

---

# useage

#### config config/proxy.js

- ***eggFramework*** If you need to resolve the proxy configuration that is dependent on the plugin at the same time, otherwise skip. If the framework you are using is based on egg for secondary encapsulation, then configure the real name of the framework you use here. The default value is `egg`.
- ***directoryToJar*** Store the file directory for jar and resources.jar， default value is `${app.root}/libs`
- ***mavenRepository*** Specify the maven repository url. Format reference path/to/maven/conf/settings.xml#profile.repository.url, like `https://repo.maven.apache.org/maven2/`.

The directoryToJar and mavenRepository are used together, starting from the specified file directoryfirst, and then finding from mavenRepository if not found (if mavenRepository is specified)

At least one directoryToJar or mavenRepository exists.

***The jar package must contain binary and source packages***

For example, the following configuration will try to find 2 jar packages `jar2proxy-facade.jar` and `jar2proxy-facade-sources.jar`.

#### config package.json

```js
"scripts": {
  "jar2proxy": "egg-bin jar2proxy"
},
```

#### run command

`$ npm run jar2proxy`

#### output files

- `app/proxy/userFacade.js` The name of the configuration will be processed by default into a camel format. You can read the interface definition and find the class definition details from the `app/proxy_class`.
- `app/proxy_class/index.js` When load for the first time, all class definitions will be merged into one file. This file is only used for egg-rpc.
- `app/proxy_class/com/ali/jar2proxy/normal/enums/xx.js`
- `app/proxy_class/com/ali/jar2proxy/normal/model/yy.js`

```js

const path = require('path');

module.exports = {
  eggFramework: 'egg',
  directoryToJar: path.join(__dirname, '../libs'),
  mavenRepository: null,
  group: 'HSF',
  version: '1.0',
  uniqueId: 'uniqueId',
  tpl: 'proxy.js.tpl',
  method: {},
  responseTimeout: 1000,
  services: [
    {
      appName: 'jar2proxy',
      api: {
        UserFacade: {
          interfaceName: 'com.ali.jar2proxy.normal.facade.UserFacade',
          group: 'HSF',
          version: '1.0',
          uniqueId: 'uniqueId',
          tpl: 'proxy.js.tpl',
          method: {},
          responseTimeout: 1000,
        },
      },
      dependency: [
        {
          groupId: 'com.alibaba.jar2proxy.facade',
          artifactId: 'jar2proxy-facade',
          version: '1.0.0',
        },
      ],
    },
  ],
};
```

Refer this [article](https://github.com/eggjs/egg-sofa-rpc/wiki/RPC-%E4%BB%A3%E7%90%86%EF%BC%88Proxy%EF%BC%89%E9%85%8D%E7%BD%AE) for more details.