package com.ali.jar2proxy.astparser.model.description;

import com.ali.jar2proxy.astparser.model.description.base.TypeDescription;
import com.sun.tools.javadoc.FieldDocImpl;

import java.util.HashMap;
import java.util.Map;

public class EnumElementDescription extends TypeDescription {

  private String fieldName;
  private Map<String, Object> enumValue = new HashMap<String, Object>();

  /**
   * @param doc
   */
  public void parse(Object doc) throws Exception {
    FieldDocImpl fieldDoc = (FieldDocImpl) doc;
    this.fieldName = fieldDoc.name();
    this.canonicalName = fieldDoc.type().qualifiedTypeName();
    // enum's name is only property for hessian serialization and deserialization
    // $name means the enum element name
    this.enumValue.put("$name", fieldDoc.name());
    // name default value is same as $name but developer may modify it with self define property "name"
    this.enumValue.put("name", fieldDoc.name());
    this.commentText = fieldDoc.getRawCommentText();
  }

  public Map<String, Object> getEnumValue() {
    return enumValue;
  }

  public void setEnumValue(Map<String, Object> enumValue) {
    this.enumValue = enumValue;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
}
