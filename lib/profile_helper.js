'use strict';

class ProfileHelper {
  constructor(profile) {
    this._profile = profile;
    this._dependencies = null;
  }

  getDependencies(types) {
    this._dependencies = new Set();

    for (const item of types) {
      this._getDependencies(item);
    }
    return {
      interfaces: Array.from(this._dependencies).map(canonicalName => {
        let intf = this._profile.classMap[canonicalName];
        if (!intf) {
          intf = this._profile.enumMap[canonicalName];
        }
        // 抽象类不需要引入
        if (intf && intf.abstractClass) return null;

        return intf;
      }).filter(item => !!item),
    };
  }

  // 递归读取泛型依赖
  _handleTypeInGeneric(generic) {
    if (!Array.isArray(generic)) {
      return;
    }

    for (const item of generic) {
      if (item.generic) {
        this._handleTypeInGeneric(item.generic);
      }

      this._getDependencies(item.type);
    }
  }

  _getDependencies(canonicalName) {
    // TODO:
    if (!canonicalName) return;

    // 枚举
    if (this._profile.enumMap[canonicalName]) {
      return this._dependencies.add(canonicalName);
    }

    // 防止循环引用
    if (this._dependencies.has(canonicalName)) {
      return;
    }

    // 类型声明
    // if (Array.isArray(this._profile.declareMap[canonicalName]) && !canonicalName.startsWith('java.util.')) {
    //   for (const item of this._profile.declareMap[canonicalName]) {
    //     this._getDependencies(item);
    //   }
    //   this._types.add(canonicalName);
    //   return;
    // }
    const classMap = this._profile.classMap[canonicalName];
    this._dependencies.add(canonicalName);

    if (!classMap || !classMap.canonicalName) {
      return null;
    }

    // 抽象类可能没用 fields
    if (Array.isArray(classMap.fields)) {
      for (const field of classMap.fields) {
        if (canonicalName === field.canonicalName) {
          return;
        }

        // 泛型处理
        if (field.generic) {
          this._handleTypeInGeneric(field.generic);
        }

        this._getDependencies(field.canonicalName);
      }
    }
  }
}

module.exports = ProfileHelper;
