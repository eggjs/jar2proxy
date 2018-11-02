package com.ali.jar2proxy.astparser.model.description.parser;

import com.ali.jar2proxy.astparser.model.description.base.ParamDescription;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SymbolVarSymbol {

  private static Logger logger = LogManager.getLogger();

  public static void parse(Symbol symbol, ParamDescription desc) {
    Symbol.VarSymbol varSymbol = (Symbol.VarSymbol) symbol;
    Type type = varSymbol.type;
    if (type instanceof Type.ArrayType) {
      // String[]
      TypeArrayType result = TypeArrayType.parse(type, desc);
      desc.setCanonicalName(result.getCanonicalName());
      desc.setIsArray(true);
      desc.setArrayDepth(result.getArrayDepth());
      desc.setIsEnum(result.getIsEnum());

    } else if (type instanceof Type.ClassType) {
      // Map<Integer, String[]>
      TypeClassType result = TypeClassType.parse(type, desc);
      desc.setCanonicalName(result.getCanonicalName());
      desc.setGeneric(result.getGeneric());
      desc.setIsEnum(result.getIsEnum());
      desc.setListType(result.getListType());
      desc.setMapType(result.getMapType());

      if (!Boolean.TRUE.equals(desc.getListType()) && !Boolean.TRUE.equals(desc.getMapType())) {
        desc.setAbstractClass(result.getAbstractClass());
      }

    } else if (type instanceof Type.TypeVar) {
      // generic alias parse
      // When field type is alias like T, S
      Type.TypeVar typeVar = (Type.TypeVar) type;
      desc.setTypeAlias(typeVar.toString());
      desc.setTypeAliasIndex(desc.getModel().getTyparamsMap().get(desc.getTypeAlias()));

    } else if (type instanceof Type.JCPrimitiveType) {
      // like boolean primitiveType
      desc.setCanonicalName(type.toString());
    } else {
      logger.warn("unknow type ------------------------------------" + type.getClass().getCanonicalName());
    }
  }

}
