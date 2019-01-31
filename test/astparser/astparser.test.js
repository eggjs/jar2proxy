'use strict';

require('./global');
const JarProcessor = require('../../lib/java/jar');
const { join } = require('path');
const os = require('os');
const mkdirp = require('mkdirp');
const assert = require('assert');
const fs = require('fs');

describe('test/astparser/astparser.test.js', () => {

  let classMap;
  let enumMap;
  let proxyMap;
  let declareMap;

  before(() => {
    const tmpSourceDir = join(os.tmpdir(), Date.now().toString());
    mkdirp.sync(tmpSourceDir);
    const sourceFile = join(__dirname, '../fixtures/facade/target/facade-sources.jar');
    const jarFile = join(__dirname, '../fixtures/facade/target/facade.jar');
    const jar = new JarProcessor({
      logger: require('../../lib/logger')(join(__dirname, '../../')),
    });
    jar.copyTo(sourceFile, tmpSourceDir);
    jar.copyTo(jarFile, tmpSourceDir);
    jar.extract([ sourceFile ], tmpSourceDir);
    const ast = jar.parse(tmpSourceDir, {
      services: [
        {
          api: {
            userFacade: 'com.ali.jar2proxy.normal.facade.UserFacade',
            topicConcernFacade: 'com.ali.jar2proxy.generic.facade.TopicConcernFacade',
          },
        },
      ],
    });
    assert(/\.json$/.test(ast));
    assert(fs.existsSync(ast));
    const astJson = require(ast);
    classMap = astJson.classMap;
    enumMap = astJson.enumMap;
    proxyMap = astJson.proxyMap;
    declareMap = astJson.declareMap;
  });

  describe('com.ali.jar2proxy.generic.model.TMgetPopCountRequest', () => {

    it('typeIdList', () => {

      const clz = classMap['com.ali.jar2proxy.generic.model.TMgetPopCountRequest'];
      const field = clz.fields.find(field => field.fieldName === 'typeIdList');

      assert(field.generic.length === 1, 'java.util.List generic should have only one element');
      assert(field.generic[0].type === 'com.ali.jar2proxy.generic.model.Pair');

      assert(field.generic[0].generic.length === 2, 'Pair\'s generic should have two elements');
      assert(field.generic[0].generic[0].type === 'com.ali.jar2proxy.generic.enums.TOPIC_TYPE');
      assert(field.generic[0].generic[1].type === 'java.lang.String');

      assert(field.generic[0].generic[0].isEnum === true, 'enum class should have property `isEnum: true`');
    });

  });

  describe('com.ali.jar2proxy.generic.model.TTopicTopRequest', () => {

    it('topicType', () => {
      const clz = classMap['com.ali.jar2proxy.generic.model.TTopicTopRequest'];
      const field = clz.fields.find(item => item.fieldName === 'topicType');
      assert(field.isEnum === true, 'field property isEnum should be true');
    });

  });

  it('com.ali.jar2proxy.normal.model.AbstractUser', () => {
    const clz = classMap['com.ali.jar2proxy.normal.model.AbstractUser'];
    assert(clz.abstractClass === true, 'abstract class should true');
  });

  it('com.ali.jar2proxy.generic.model.Pair', () => {
    const clz = classMap['com.ali.jar2proxy.generic.model.Pair'];
    const fieldFirst = clz.fields.find(item => item.fieldName === 'first');
    const fieldSecond = clz.fields.find(item => item.fieldName === 'second');
    assert(fieldFirst.typeAlias === 'K');
    assert(fieldFirst.typeAliasIndex === 0);
    assert(fieldFirst.commentText === ' first object\n');
    assert(fieldSecond.typeAlias === 'V');
    assert(fieldSecond.typeAliasIndex === 1);
    assert(fieldSecond.commentText === ' second object\n');
  });

  describe('com.ali.jar2proxy.normal.model.UserInfo', () => {

    let clz;
    before(() => {
      clz = classMap['com.ali.jar2proxy.normal.model.UserInfo'];
    });

    it('inputProps generic array parse should ok', () => {
      const field = clz.fields.find(item => item.fieldName === 'inputProps');
      assert(field.canonicalName === 'java.util.Map');
      assert(field.generic.length === 2);
      assert(field.generic[0].type === 'java.lang.Integer');
      assert(field.generic[1].type === 'java.lang.String');
      assert(field.generic[1].isArray === true);
      assert(field.generic[1].arrayDepth === 1);
    });

    it('deepArray array parse should ok', () => {
      const field = clz.fields.find(item => item.fieldName === 'deepArray');
      assert(field.canonicalName === 'java.lang.String');
      assert(field.isArray === true);
      assert(field.arrayDepth === 2);
    });
  });

  describe('com.ali.jar2proxy.extend.model.PayToolBaseConsultRequest', () => {

    let clz;
    before(() => {
      clz = classMap['com.ali.jar2proxy.extend.model.PayToolBaseConsultRequest'];
    });

    it('businessContexts generic typeparams', () => {
      const field = clz.fields.find(item => item.fieldName === 'businessContexts');
      assert(field.generic.length === 1);
      assert(field.generic[0].type === 'T');
      assert(field.generic[0].typeAlias === 'T');
      assert(field.generic[0].typeAliasIndex === 0);
    });

  });

  describe('com.ali.jar2proxy.generic.enums.TOPIC_TYPE', () => {
    let clz;
    before(() => {
      clz = enumMap['com.ali.jar2proxy.generic.enums.TOPIC_TYPE'];
    });
    it('enum INFO_TYPE_NEWS should ok', () => {
      const field = clz.fields.find(item => item.fieldName === 'INFO_TYPE_NEWS');
      assert(field.commentText === '新闻资讯的类型值 ');
      assert(field.fieldName === 'INFO_TYPE_NEWS');
      assert(field.canonicalName === 'com.ali.jar2proxy.generic.enums.TOPIC_TYPE');
      assert(field.enumValue.name === 'INFO_TYPE_NEWS');
      assert(field.enumValue._value === 0);
    });
  });

  describe('com.ali.jar2proxy.generic.enums.GetNameCase', () => {
    let clz;
    before(() => {
      clz = enumMap['com.ali.jar2proxy.generic.enums.GetNameCase'];
    });
    it('enum GET_NAME_CASE "name" override "$name" should ok', () => {
      const field = clz.fields.find(item => item.fieldName === 'GET_NAME_CASE');
      assert(field.commentText === '');
      assert(field.fieldName === 'GET_NAME_CASE');
      assert(field.canonicalName === 'com.ali.jar2proxy.generic.enums.GetNameCase');
      assert(field.enumValue.$name === 'GET_NAME_CASE');
      assert(field.enumValue.name === 'n');
      assert(field.enumValue.code === 'c');
    });
  });

  describe('com.ali.jar2proxy.normal.facade.UserFacade', () => {
    let clz;
    before(() => {
      clz = proxyMap['com.ali.jar2proxy.normal.facade.UserFacade'];
    });

    it('method queryByUserId$64207ce String type param should parse ok', () => {
      const method = clz.methods.find(item => item.uniqueId === '64207ce');
      assert(method.commentText === ' Override method queryByUserId\n @param userId\n @return UserInfo\n');
      assert(method.params.length === 1);
      assert(method.params[0].paramName === 'userId');
      assert(method.params[0].canonicalName === 'java.lang.String');
      assert(method.raw.includes(' public UserInfo queryByUserId(String userId);'));
      assert(method.returnType.canonicalName === 'com.ali.jar2proxy.normal.model.UserInfo');
    });

    it('method queryByUserId$f291304 Class type param should parse ok', () => {
      const method = clz.methods.find(item => item.uniqueId === 'f291304');
      assert(method.commentText === ' Override method queryByUserId\n @param user\n @return UserInfo\n');
      assert(method.params.length === 1);
      assert(method.params[0].paramName === 'user');
      assert(method.params[0].canonicalName === 'com.ali.jar2proxy.normal.model.UserInfo');
      assert(method.raw.includes(' public UserInfo queryByUserId(UserInfo user);'));
      assert(method.returnType.canonicalName === 'com.ali.jar2proxy.normal.model.UserInfo');
    });

    it('method queryByUserIds$f240eaf array type param should parse ok', () => {
      const method = clz.methods.find(item => item.uniqueId === 'f240eaf');
      assert(method.commentText === ' Param type is String Array\n @param userIds\n @return UserInfo\n');
      assert(method.params.length === 1);
      assert(method.params[0].paramName === 'userIds');
      assert(method.params[0].canonicalName === 'java.lang.String');
      assert(method.params[0].isArray === true);
      assert(method.params[0].arrayDepth === 1);
      assert(method.raw.includes(' public UserInfo queryByUserIds(String[] userIds);'));
      assert(method.returnType.canonicalName === 'com.ali.jar2proxy.normal.model.UserInfo');
    });

    it('method queryByUserIds$170cde7 deep array type param should parse ok', () => {
      const method = clz.methods.find(item => item.uniqueId === '170cde7');
      assert(method.commentText === ' Param type is String deep Array\n @param userIds\n @return UserInfo\n');
      assert(method.params.length === 1);
      assert(method.params[0].paramName === 'userIds');
      assert(method.params[0].canonicalName === 'java.lang.String');
      assert(method.params[0].isArray === true);
      assert(method.params[0].arrayDepth === 2);
      assert(method.raw.includes(' public UserInfo queryByUserIds(String[][] userIds);'));
      assert(method.returnType.canonicalName === 'com.ali.jar2proxy.normal.model.UserInfo');
      assert(method.isOverloading === true);
    });

    it('method queryUserInfoByGeneric$2c1680a map generic array type param should parse ok', () => {
      const method = clz.methods.find(item => item.uniqueId === '2c1680a');
      assert(method.commentText === ' Param with generic\n @param inputProps\n @return\n');
      assert(method.params.length === 1);
      assert(method.params[0].paramName === 'inputProps');
      assert(method.params[0].canonicalName === 'java.util.Map');
      assert(method.params[0].generic.length === 2);
      assert(method.params[0].generic[0].type === 'java.lang.Integer');
      assert(method.params[0].generic[1].type === 'java.lang.String');
      assert(method.params[0].generic[1].isArray === true);
      assert(method.params[0].generic[1].arrayDepth === 1);
      assert(method.raw.includes(' public UserInfo queryUserInfoByGeneric(Map<Integer, String[]> inputProps);'));
      assert(method.returnType.canonicalName === 'com.ali.jar2proxy.normal.model.UserInfo');
    });

  });

  describe('declareMap', () => {

    it('com.ali.jar2proxy.extend.model.UserConsultRequest', () => {
      const declareList = declareMap['com.ali.jar2proxy.extend.model.UserConsultRequest'];
      assert(declareList.length === 1);
      assert.equal(declareList[0], 'com.ali.jar2proxy.extend.model.UccUserCalcConsultRequest');
    });

    it('com.ali.jar2proxy.extend.model.PayToolConsultRequest', () => {
      const declareList = declareMap['com.ali.jar2proxy.extend.model.PayToolConsultRequest'];
      assert(declareList.length === 3);
      const list = [
        'com.ali.jar2proxy.extend.model.UserConsultRequest',
        'com.ali.jar2proxy.extend.model.UccUserCalcConsultRequest',
        'com.ali.jar2proxy.extend.model.PayToolBaseConsultRequest',
      ];
      list.forEach(item => {
        assert(declareList.includes(item));
      });
    });

  });

});
