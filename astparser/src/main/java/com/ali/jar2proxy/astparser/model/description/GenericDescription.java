package com.ali.jar2proxy.astparser.model.description;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author coolme200
 */
public class GenericDescription implements Generic {

  private String type;
  private String typeAlias;
  private Integer typeAliasIndex;
  private Boolean isEnum;
  @JSONField(serialize = false)
  private boolean typeVar;
  private Boolean isArray;
  private Integer arrayDepth;

  private List<GenericDescription> generic = new ArrayList<GenericDescription>();

  public Boolean getIsEnum() {
    return isEnum;
  }

  public void setIsEnum(Boolean isEnum) {
    this.isEnum = isEnum;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean getTypeVar() {
    return typeVar;
  }

  public void setTypeVar(boolean typeVar) {
    this.typeVar = typeVar;
  }

  public Boolean getIsArray() {
    return isArray;
  }

  public void setIsArray(Boolean isArray) {
    this.isArray = isArray;
  }

  public List<GenericDescription> getGeneric() {
    return generic.size() == 0 ? null : generic;
  }

  public void setGeneric(List<GenericDescription> generic) {
    this.generic = generic;
  }

  public void addGeneric(GenericDescription obj) {
    this.generic.add(obj);
  }

  public Integer getArrayDepth() {
    return arrayDepth;
  }

  public void setArrayDepth(Integer arrayDepth) {
    this.arrayDepth = arrayDepth;
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
}

