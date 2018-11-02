package com.ali.jar2proxy.astparser.model.description;

import com.ali.jar2proxy.astparser.Util;
import com.ali.jar2proxy.astparser.model.description.base.ParamDescription;
import com.ali.jar2proxy.astparser.model.description.base.TypeDescription;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javadoc.MethodDocImpl;
import com.sun.tools.javadoc.ProgramElementDocImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author coolme200
 */
public class MethodDescription extends TypeDescription {

  private Logger logger = LogManager.getLogger();
  protected String raw;
  protected String methodName;
  protected String uniqueId;
  protected ReturnTypeDescription returnType;
  protected List<ArgumentDescription> params = new ArrayList<ArgumentDescription>();
  protected Map<String, Map> annotations = new HashMap<String, Map>();
  protected Boolean isOverloading;
  private Map genericMap;

  public void parse(Object doc) throws Exception {
    MethodDocImpl methodDoc = (MethodDocImpl) doc;
    // this.parseZoneroute(method);
    Field treeField = ProgramElementDocImpl.class.getDeclaredField("tree");
    treeField.setAccessible(true);
    JCTree jctree = (JCTree) treeField.get(methodDoc);

    this.setRaw(jctree.toString().replaceAll("\n", " "));
    this.setCommentText(methodDoc.getRawCommentText());
    this.setMethodName(methodDoc.name());
    this.parseParams(methodDoc);
    // this.parseRouteParameter(method);
    this.parseReturnType(methodDoc);
    // parse method annotations
    this.parseMethodAnnotation(methodDoc);
    // parse param annotations
    this.setUniqueId(this.genUniqueId());
  }

  private String genUniqueId() throws UnsupportedEncodingException, NoSuchAlgorithmException {
    String typeString = this.methodName + ",";
    for (int i = 0; i < this.params.size(); i++) {
      ParamDescription param = this.params.get(i);
      String arrstr = "";
      if (Boolean.TRUE.equals(param.getIsArray())) {
        for (int j = 0; j < param.getArrayDepth(); j++) {
          arrstr += "[]";
        }
      }
      typeString += param.getCanonicalName() + arrstr;
      if (i < this.params.size() - 1) {
        typeString += ",";
      }
    }
    String md5 = Util.md5(typeString);
    return md5.substring(0, 7);
  }

  private void parseMethodAnnotation(MethodDocImpl methodDoc) {
    AnnotationDesc[] descArray = methodDoc.annotations();
    if (descArray.length <= 0) {
      return;
    }
    for (AnnotationDesc desc : descArray) {
      // parse values
      Map an = new HashMap();
      AnnotationDesc.ElementValuePair[] pairs = desc.elementValues();
      for (AnnotationDesc.ElementValuePair pair : pairs) {
        an.put(pair.element().name(), pair.value());
      }
      // an.put();
      this.annotations.put(desc.annotationType().toString(), an);
    }
    logger.debug(descArray.length);
  }

  private void parseParams(MethodDocImpl methodDoc) throws NoSuchFieldException, IllegalAccessException {
    Parameter[] params = methodDoc.parameters();
    for (Parameter param : params) {
      ArgumentDescription paramDesc = new ArgumentDescription();
      paramDesc.parse(param);
      this.params.add(paramDesc);
    }
  }

  private void parseReturnType(MethodDocImpl method) throws Exception {
    ReturnTypeDescription returnTypeDesc = new ReturnTypeDescription();
    returnTypeDesc.parse(method);
    this.setReturnType(returnTypeDesc);
  }

  public String getRaw() {
    return raw;
  }

  public void setRaw(String raw) {
    this.raw = raw;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public ReturnTypeDescription getReturnType() {
    return returnType;
  }

  public void setReturnType(ReturnTypeDescription returnType) {
    this.returnType = returnType;
  }

  public List<ArgumentDescription> getParams() {
    return params;
  }

  public void setParams(List<ArgumentDescription> params) {
    this.params = params;
  }

  public void setGenericMap(Map genericMap) {
    this.genericMap = genericMap;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public Boolean getIsOverloading() {
    return this.isOverloading;
  }

  public void setIsOverloading(Boolean isOverloading) {
    this.isOverloading = isOverloading;
  }
}
