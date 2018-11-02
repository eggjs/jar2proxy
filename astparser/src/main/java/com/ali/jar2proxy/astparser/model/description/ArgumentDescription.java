package com.ali.jar2proxy.astparser.model.description;

import com.ali.jar2proxy.astparser.Reflect;
import com.ali.jar2proxy.astparser.model.description.base.ParamDescription;
import com.ali.jar2proxy.astparser.model.description.parser.SymbolVarSymbol;
import com.sun.javadoc.Parameter;
import com.sun.tools.javac.code.Symbol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentDescription extends ParamDescription {

  private Logger logger = LogManager.getLogger();

  protected String paramName;

  /**
   * @param doc
   */
  public void parse(Object doc) throws NoSuchFieldException, IllegalAccessException {
    Parameter param = (Parameter) doc;
    this.setParamName(param.name());
    Symbol symbol = Reflect.getSym(param);
    if (symbol instanceof Symbol.VarSymbol) {
      SymbolVarSymbol.parse(symbol, this);
    } else {
      logger.error("unknow type -----------------------------------------------");
    }

  }

  public String getParamName() {
    return paramName;
  }

  public void setParamName(String paramName) {
    this.paramName = paramName;
  }


}
