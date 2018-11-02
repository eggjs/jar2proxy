package com.ali.jar2proxy.astparser.model.description;

import com.ali.jar2proxy.astparser.AstParser;
import com.ali.jar2proxy.astparser.Reflect;
import com.ali.jar2proxy.astparser.model.description.base.ParamDescription;
import com.ali.jar2proxy.astparser.model.description.parser.SymbolVarSymbol;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javadoc.FieldDocImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FieldDescription extends ParamDescription {

  private Logger logger = LogManager.getLogger();

  private String fieldName;
  private Object defaultValue;
  private Object constantValue;
  private Boolean isStatic;
  private Boolean isFinal;
  private Boolean isTransient;

  public FieldDescription(ModelDescription model) {
    this.model = model;
  }

  /**
   * @param doc
   */
  public void parse(Object doc) throws Exception {
    FieldDocImpl fieldDoc = (FieldDocImpl) doc;
    AstParser.threadLocal.get().addType(fieldDoc.type().asClassDoc());

    if (fieldDoc.isStatic()) {
      this.setIsStatic(true);
    }
    if (fieldDoc.isFinal()) {
      this.setIsFinal(true);
    }
    if (fieldDoc.isTransient()) {
      this.setIsTransient(true);
    }
    if (fieldDoc.constantValue() != null) {
      this.setConstantValue(fieldDoc.constantValue());
    }

    this.setFieldName(fieldDoc.name());
    this.setCommentText(fieldDoc.getRawCommentText());
    Symbol symbol = Reflect.getSym(doc);
    if (symbol instanceof Symbol.VarSymbol) {
      SymbolVarSymbol.parse(symbol, this);
    } else {
      logger.warn("unknow type " + symbol.getClass().getCanonicalName());
    }

    logger.debug(this.getFieldName() + " " + this.getCanonicalName());

  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public Boolean getIsStatic() {
    return isStatic;
  }

  public void setIsStatic(Boolean aStatic) {
    isStatic = aStatic;
  }

  public Boolean getIsFinal() {
    return isFinal;
  }

  public void setIsFinal(Boolean aFinal) {
    isFinal = aFinal;
  }

  public Boolean getIsTransient() {
    return isTransient;
  }

  public void setIsTransient(Boolean aTransient) {
    isTransient = aTransient;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(Object defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Object getConstantValue() {
    return constantValue;
  }

  public void setConstantValue(Object constantValue) {
    this.constantValue = constantValue;
  }

}
