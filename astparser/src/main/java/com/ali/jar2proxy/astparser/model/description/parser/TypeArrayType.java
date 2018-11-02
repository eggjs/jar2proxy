package com.ali.jar2proxy.astparser.model.description.parser;

import com.ali.jar2proxy.astparser.model.description.base.ParamDescription;
import com.sun.tools.javac.code.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TypeArrayType {

  private static Logger logger = LogManager.getLogger();

  private String canonicalName;
  private Boolean isEnum;
  private Integer arrayDepth = 0;

  // String[] arg
  public static TypeArrayType parse(Type type, ParamDescription desc) {
    TypeArrayType result = new TypeArrayType();
    Type.ArrayType arrayType = (Type.ArrayType) type;
    Type elementtype = arrayType.elemtype;
    if (elementtype instanceof Type.ArrayType) {

      TypeArrayType elementResult = TypeArrayType.parse(elementtype, desc);
      result.canonicalName = elementResult.getCanonicalName();

    } else if (elementtype instanceof Type.ClassType) {

      TypeClassType elementResult = TypeClassType.parse(elementtype, desc);
      result.canonicalName = elementResult.getCanonicalName();
      result.isEnum = elementResult.getIsEnum();

    } else if (elementtype instanceof Type.JCPrimitiveType) {

      result.canonicalName = elementtype.toString();

    } else {
      logger.warn("unknow type --------------------------------------" + elementtype.getClass().getCanonicalName());
    }
    result.arrayDepth = result.countArrayDepth(type.toString());
    // isEnum
    return result;
  }

  // String[][] => 2
  public static int countArrayDepth(String str) {
    String subStr = "[]";
    if(str != null && str.length() != 0) {
      int count = 0;
      for(int index = 0; (index = str.indexOf(subStr, index)) != -1; index += subStr.length()) {
        ++count;
      }
      return count;
    } else {
      return 0;
    }
  }

  public String getCanonicalName() {
    return canonicalName;
  }

  public void setCanonicalName(String canonicalName) {
    this.canonicalName = canonicalName;
  }

  public int getArrayDepth() {
    return arrayDepth;
  }

  public void setArrayDepth(int arrayDepth) {
    this.arrayDepth = arrayDepth;
  }

  public Boolean getIsEnum() {
    return isEnum;
  }

  public void setIsEnum(Boolean anEnum) {
    isEnum = anEnum;
  }
}
