package com.ali.jar2proxy.astparser.model.description.parser;

import com.ali.jar2proxy.astparser.AstParser;
import com.ali.jar2proxy.astparser.model.description.GenericDescription;
import com.ali.jar2proxy.astparser.model.description.ModelDescription;
import com.ali.jar2proxy.astparser.model.description.base.ParamDescription;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javadoc.ClassDocImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TypeClassType {

  private static Logger logger = LogManager.getLogger();
  private Boolean isEnum;
  // is Map's subClass
  private Boolean mapType;
  // is Collection's subClass
  private Boolean listType;
  private Boolean abstractClass;
  private String canonicalName;
  private List<GenericDescription> generic = new ArrayList<GenericDescription>();

  public static TypeClassType parse(Type type, ParamDescription desc) {
    TypeClassType result = new TypeClassType();
    Type.ClassType classType = (Type.ClassType) type;
    result.canonicalName = classType.tsym.flatName().toString();
    AstParser parser = AstParser.threadLocal.get();
    // try addType this current type if not parse.
    ClassDocImpl doc = new ClassDocImpl(parser.getDocEnv(), (Symbol.ClassSymbol) classType.tsym);
    parser.addType(doc);

    // isEnum
    if (parser.getOutput().getEnumMap().containsKey(result.canonicalName)) {
      result.setIsEnum(true);
    }
    ModelDescription model = (ModelDescription) parser.getOutput().getClassMap().get(result.canonicalName);
    if (model != null) {
      // listType
      result.setListType(model.getListType());
      // mapType
      result.setMapType(model.getMapType());
      result.setAbstractClass(model.getAbstractClass());
    }

    // parse generic
    Iterator<Type> iter = classType.typarams_field.iterator();
    while (iter.hasNext()) {
      Type typeparam = iter.next();
      if (typeparam instanceof Type.ClassType) {

        TypeClassType typeparamResult = TypeClassType.parse(typeparam, desc);

        GenericDescription gen = new GenericDescription();
        gen.setType(typeparamResult.getCanonicalName());
        gen.setGeneric(typeparamResult.getGeneric());
        gen.setIsEnum(typeparamResult.getIsEnum());
        result.generic.add(gen);

      } else if (typeparam instanceof Type.ArrayType) {

        TypeArrayType typeparamResult = TypeArrayType.parse(typeparam, desc);

        GenericDescription gen = new GenericDescription();
        gen.setType(typeparamResult.getCanonicalName());
        gen.setIsArray(true);
        gen.setArrayDepth(typeparamResult.getArrayDepth());
        gen.setIsEnum(typeparamResult.getIsEnum());
        result.generic.add(gen);

      } else if (typeparam instanceof Type.TypeVar) {
        // varible type of generic typeparams
        GenericDescription gen = new GenericDescription();
        gen.setType(typeparam.toString());
        gen.setTypeAlias(typeparam.toString());
        gen.setTypeAliasIndex(desc.getModel().getTyparamsMap().get(gen.getTypeAlias()));
        gen.setTypeVar(true);
        result.generic.add(gen);

      } else {
        logger.warn("unknow type --------------------------- " + typeparam.getClass().getCanonicalName());
      }
    }
    return result;
  }

  public String getCanonicalName() {
    return canonicalName;
  }

  public void setCanonicalName(String canonicalName) {
    this.canonicalName = canonicalName;
  }

  public List<GenericDescription> getGeneric() {
    return generic;
  }

  public void setGeneric(List<GenericDescription> generic) {
    this.generic = generic;
  }

  public Boolean getIsEnum() {
    return isEnum;
  }

  public void setIsEnum(Boolean anEnum) {
    isEnum = anEnum;
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
