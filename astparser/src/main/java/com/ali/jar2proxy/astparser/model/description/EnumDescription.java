package com.ali.jar2proxy.astparser.model.description;


import com.ali.jar2proxy.astparser.Reflect;
import com.ali.jar2proxy.astparser.model.description.base.TypeDescription;
import com.alibaba.fastjson.annotation.JSONField;
import com.sun.tools.javadoc.ClassDocImpl;
import com.sun.tools.javadoc.FieldDocImpl;

import java.util.ArrayList;
import java.util.List;

public class EnumDescription extends TypeDescription {

  private List<EnumElementDescription> fields = new ArrayList<EnumElementDescription>();

  @JSONField(serialize = false)
  private List<EnumElementDescription> enumFields = new ArrayList<EnumElementDescription>();

  /**
   * @param doc
   */
  public void parse(Object doc) throws Exception {
    ClassDocImpl clzdoc = (ClassDocImpl) doc;
    this.setCanonicalName(Reflect.getTsym(clzdoc).flatName().toString());
    this.setCommentText(clzdoc.getRawCommentText());

    FieldDocImpl[] fields = (FieldDocImpl[]) clzdoc.fields(false);
    int ordinal = 0;
    for (FieldDocImpl fieldDoc : fields) {
      EnumElementDescription desc = new EnumElementDescription();
      desc.parse(fieldDoc);
      if (this.getCanonicalName().equals(desc.getCanonicalName())) {
        // enum's fieldType is equal to class type
        desc.getEnumValue().put("ordinal", ordinal++);
        this.fields.add(desc);
      } else {
        this.enumFields.add(desc);
      }
    }
  }

  public List<EnumElementDescription> getFields() {
    return fields;
  }

  public void setFields(List<EnumElementDescription> fields) {
    this.fields = fields;
  }

  public List<EnumElementDescription> getEnumFields() {
    return enumFields;
  }

  public void setEnumFields(List<EnumElementDescription> enumFields) {
    this.enumFields = enumFields;
  }
}
