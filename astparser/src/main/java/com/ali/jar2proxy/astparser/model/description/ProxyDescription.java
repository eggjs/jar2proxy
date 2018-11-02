package com.ali.jar2proxy.astparser.model.description;

import com.ali.jar2proxy.astparser.model.description.base.TypeDescription;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javadoc.ClassDocImpl;
import com.sun.tools.javadoc.MethodDocImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyDescription extends TypeDescription {

  private Logger logger = LogManager.getLogger();
  protected List<MethodDescription> methods = new ArrayList<MethodDescription>();
  protected Map annotation;

  public void parse(Object doc) throws Exception {
    ClassDocImpl clzdoc = (ClassDocImpl) doc;
    this.setCommentText(clzdoc.getRawCommentText());
    this.setCanonicalName(clzdoc.qualifiedName());
    this.parseMethods(clzdoc, true, new HashMap());
    this.overloading();
  }

  private void overloading() {
    Map<String, MethodDescription> keyMap = new HashMap<String, MethodDescription>();
    for (MethodDescription method : this.getMethods()) {
      // if the method appear twice
      if (keyMap.containsKey(method.getMethodName())) {
        method.setIsOverloading(true);
        keyMap.get(method.getMethodName()).setIsOverloading(true);
      } else {
        // only record the first appear method
        keyMap.put(method.getMethodName(), method);
      }
      this.logger.debug("overloading method " + method.getMethodName() + ": " + method.getIsOverloading());
    }
  }

  private void parseMethods(ClassDocImpl clz, boolean deep, Map genericMap) throws Exception {
    MethodDocImpl[] methods = (MethodDocImpl[]) clz.methods();
    for (MethodDocImpl methodDoc : methods) {
      if (!methodDoc.isPublic()) {
        continue;
      }
      MethodDescription method = new MethodDescription();
      method.setGenericMap(genericMap);
      method.parse(methodDoc);
      this.methods.add(method);
    }
    if (!deep) {
      return;
    }
    List<Type> superTypes = clz.type.interfaces_field;
    for (Type type : superTypes) {
      // 泛型引用
      List<Type> generic = type.tsym.type.allparams();
      // 实际引用
      List<Type> real = type.allparams();
      HashMap gen = new HashMap();
      int index = 0;
      for (Type a : generic) {
        // gen.put(a, new ClassDocImpl(ProxyDoclet.env, real.get(index++).tsym.enclClass()));
      }
      // ClassDocImpl doc = new ClassDocImpl(ProxyDoclet.env, type.tsym.enclClass());
      // this.parseMethods(doc, true, gen);
    }
  }

  public List<MethodDescription> getMethods() {
    return methods;
  }

  public void setMethods(List<MethodDescription> methods) {
    this.methods = methods;
  }

}
