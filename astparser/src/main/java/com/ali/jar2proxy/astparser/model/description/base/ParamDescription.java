package com.ali.jar2proxy.astparser.model.description.base;

import com.ali.jar2proxy.astparser.model.description.Generic;
import com.ali.jar2proxy.astparser.model.description.GenericDescription;
import com.ali.jar2proxy.astparser.model.description.ModelDescription;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. method returnType
 * 2. method Argument
 * 3. class Field
 */
public class ParamDescription extends TypeDescription implements Generic {

  private Logger logger = LogManager.getLogger();

  @JSONField(serialize=false)
  protected ModelDescription model;

  protected Boolean isEnum;
  protected Boolean isArray;
  protected Boolean abstractClass;
  // is Map's subClass
  protected Boolean mapType;
  // is Collection's subClass
  protected Boolean listType;
  protected Integer arrayDepth;
  // generic type
  // >>
  // If class define like below:
  // >>
  // public class Pair<K, V> {
  //   private K first;
  //   private V second;
  // }
  // >>
  // then parse field output is below:
  // >>
  // field first => typeAlias: K, typeAliasIndex: 0
  // field second => typeAlias: V, typeAliasIndex: 1
  // >>
  protected String typeAlias;
  protected Integer typeAliasIndex;
  // when use
  // >>
  //{
  //  "fieldName":"list",
  //	"generic":[
  //	  {
  //		  "type":"a.b.Pair",
  //			"generic":[
  //			  {
  //				  "type":"a.b.TypeK"
  //				},
  //				{
  //				  "type":"a.b.TypeV"
  //				}
  //			]
  //		}
  //	],
  //	"canonicalName":"java.util.List"
  //}
  protected List<GenericDescription> generic = new ArrayList<GenericDescription>();

  /**
   * @param doc
   */
  public void parse(Object doc) throws Exception {
    logger.warn("TODO: override in sub class");
  }

  public void addGeneric(GenericDescription gen) {
    this.generic.add(gen);
  }

  public Boolean getIsArray() {
    return isArray;
  }

  public void setIsArray(Boolean isArray) {
    this.isArray = isArray;
  }

  public Integer getArrayDepth() {
    return arrayDepth;
  }

  public void setArrayDepth(Integer arrayDepth) {
    this.arrayDepth = arrayDepth;
  }

  public Boolean getIsEnum() {
    return isEnum;
  }

  public void setIsEnum(Boolean anEnum) {
    isEnum = anEnum;
  }

  public String getTypeAlias() {
    return typeAlias;
  }

  public void setTypeAlias(String typeAlias) {
    this.typeAlias = typeAlias;
  }

  public Integer getTypeAliasIndex() {
    return typeAliasIndex;
  }

  public void setTypeAliasIndex(Integer typeAliasIndex) {
    this.typeAliasIndex = typeAliasIndex;
  }

  public List<GenericDescription> getGeneric() {
    return generic.size() == 0 ? null : generic;
  }

  public void setGeneric(List<GenericDescription> generic) {
    this.generic = generic;
  }

  public ModelDescription getModel() {
    return model;
  }

  public void setModel(ModelDescription model) {
    this.model = model;
  }

  public Boolean getMapType() {
    return mapType;
  }

  public void setMapType(Boolean mapType) {
    this.mapType = mapType;
  }

  public Boolean getListType() {
    return listType;
  }

  public void setListType(Boolean listType) {
    this.listType = listType;
  }

  public Boolean getAbstractClass() {
    return abstractClass;
  }

  public void setAbstractClass(Boolean abstractClass) {
    this.abstractClass = abstractClass;
  }
}
