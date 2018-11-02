package com.ali.jar2proxy.astparser.model.description;

import com.ali.jar2proxy.astparser.AstParser;
import com.ali.jar2proxy.astparser.Reflect;
import com.ali.jar2proxy.astparser.model.description.base.TypeDescription;
import com.alibaba.fastjson.annotation.JSONField;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javadoc.ClassDocImpl;
import com.sun.tools.javadoc.FieldDocImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ModelDescription extends TypeDescription {

  private Logger logger = LogManager.getLogger();

  // is Map's subClass
  private Boolean mapType;
  // is Collection's subClass
  private Boolean listType;
  protected Boolean abstractClass;

  private List<FieldDescription> fields = new ArrayList<FieldDescription>();

  // public class Pair<K, V>
  // >> { K: 0, V: 1 }
  // Set field typeAliasIndex when parse generic field
  @JSONField(serialize = false)
  protected Map<String, Integer> typaramsMap = new HashMap<String, Integer>();

  // class global typaramIndex
  @JSONField(serialize = false)
  protected int typaramsMapIndex = 0;

  @JSONField(serialize = false)
  protected List<GenericDescription> generic = new ArrayList<GenericDescription>();

  @JSONField(serialize = false)
  private Map<String, com.sun.javadoc.Type> superTypesMap;

  /**
   * @param doc
   */
  public void parse(Object doc) throws Exception {
    ClassDocImpl clzdoc = (ClassDocImpl) doc;
    if (superTypesMap.containsKey("java.util.Collection")) {
      this.setListType(true);
    }
    if (superTypesMap.containsKey("java.util.Map")) {
      this.setMapType(true);
    }
    if (clzdoc.isAbstract() || clzdoc.isInterface()) {
      this.setAbstractClass(true);
    }
    this.setCanonicalName(Reflect.getTsym(clzdoc).flatName().toString());
    this.genericParse(clzdoc);
    this.parseSuperType(clzdoc);
    this.parseField(clzdoc);
  }

  /**
   * generic parse for class type
   * public class Pair<K, V> { ... }
   */
  private void genericParse(ClassDocImpl doc) {
    Iterator<Type> iter = doc.type.getTypeArguments().iterator();
    while (iter.hasNext()) {
      Type type = iter.next();
      this.typaramsMap.put(type.toString(), this.typaramsMapIndex++);
    }
  }

  private void parseSuperType(ClassDocImpl doc) throws Exception {
    Type superclassType = doc.type.supertype_field;
    if (superclassType == null || superclassType.tsym == null) {
      return;
    }
    // superclass generic
    Iterator iter = superclassType.getTypeArguments().iterator();
    while (iter.hasNext()) {
      Type type = (Type) iter.next();
      GenericDescription desc = new GenericDescription();
      desc.setType(type.toString());
      this.generic.add(desc);
    }
    if (superclassType.tsym instanceof Symbol.ClassSymbol) {
      ClassDocImpl superclassDoc = new ClassDocImpl(AstParser.threadLocal.get().getDocEnv(), (Symbol.ClassSymbol) superclassType.tsym);
      this.genericParse(superclassDoc);
      this.parseSuperType(superclassDoc);
      // do not parse fields of like List, Map
      this.parseField(superclassDoc);
    } else {
      logger.warn("unknow type -------------------------------------");
    }

  }

  /**
   * parse every field in class
   * @param doc
   * @throws Exception
   */
  private void parseField(ClassDocImpl doc) throws Exception {
    FieldDocImpl[] fields = (FieldDocImpl[]) doc.fields(false);
    for (FieldDocImpl field : fields) {
      String fieldName = field.name();
      if ("serialVersionUID".equalsIgnoreCase(fieldName)) {
        continue;
      }
      FieldDescription desc = new FieldDescription(this);
      desc.parse(field);

      // class SuperClass<T> {
      //   private List<T> attr;
      // }
      // class SubClass extends SuperClass<CurrentType> {
      //
      // }
      // T type use current CurrentType
      if (desc.getGeneric() != null) {
        for (GenericDescription genDesc : desc.getGeneric()) {
          Integer typeAliasIndex = this.typaramsMap.get(genDesc.getTypeAlias());
          if (typeAliasIndex != null && genDesc.getTypeVar() && this.generic.size() > typeAliasIndex) {
            genDesc.setType(this.generic.get(typeAliasIndex).getType());
            genDesc.setTypeVar(false);
            genDesc.setTypeAlias(null);
            genDesc.setTypeAliasIndex(null);
          }
        }
      }
      this.fields.add(desc);
    }
  }

  public List<FieldDescription> getFields() {
    return fields;
  }

  public void setFields(List<FieldDescription> fields) {
    this.fields = fields;
  }

  public Map<String, Integer> getTyparamsMap() {
    return typaramsMap;
  }

  public void setTyparamsMap(Map<String, Integer> typaramsMap) {
    this.typaramsMap = typaramsMap;
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

  public Map<String, com.sun.javadoc.Type> getSuperTypesMap() {
    return superTypesMap;
  }

  public void setSuperTypesMap(Map<String, com.sun.javadoc.Type> superTypesMap) {
    this.superTypesMap = superTypesMap;
  }
}
