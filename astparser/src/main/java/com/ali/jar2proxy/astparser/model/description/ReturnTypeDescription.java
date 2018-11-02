package com.ali.jar2proxy.astparser.model.description;

import com.ali.jar2proxy.astparser.AstParser;
import com.ali.jar2proxy.astparser.model.description.base.ParamDescription;
import com.ali.jar2proxy.astparser.model.description.parser.TypeArrayType;
import com.ali.jar2proxy.astparser.model.description.parser.TypeClassType;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javadoc.ClassDocImpl;
import com.sun.tools.javadoc.MethodDocImpl;
import com.sun.tools.javadoc.ProgramElementDocImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

/**
 * @author coolme200
 */
public class ReturnTypeDescription extends ParamDescription {

  private Logger logger = LogManager.getLogger();

  private Boolean isEnum;

  /**
   * @param doc
   */
  public void parse(Object doc) throws Exception {
    MethodDocImpl methodDoc = (MethodDocImpl) doc;
    ClassDocImpl returnTypeClassDoc = (ClassDocImpl) methodDoc.returnType().asClassDoc();
    this.setCanonicalName(returnTypeClassDoc.qualifiedTypeName());

    AstParser.threadLocal.get().addType(returnTypeClassDoc);
    if (this.ifTypeEnum()) {
      this.isEnum = true;
    }

    Field treeField = ProgramElementDocImpl.class.getDeclaredField("tree");
    treeField.setAccessible(true);
    JCTree.JCMethodDecl jctree = (JCTree.JCMethodDecl) treeField.get(methodDoc);
    Type restype = jctree.restype.type;
    if (restype instanceof Type.ClassType) {
      // Map<Integer, String[]>
      TypeClassType result = TypeClassType.parse(restype, this);
      this.setCanonicalName(result.getCanonicalName());
      this.setGeneric(result.getGeneric());
      this.setIsEnum(result.getIsEnum());

      this.setListType(result.getListType());
      this.setMapType(result.getMapType());

      if (!Boolean.TRUE.equals(this.getListType()) && !Boolean.TRUE.equals(this.getMapType())) {
        this.setAbstractClass(result.getAbstractClass());
      }

    } else if (restype instanceof Type.ArrayType) {
      // String[]
      TypeArrayType result = TypeArrayType.parse(restype, this);
      this.setCanonicalName(result.getCanonicalName());
      this.setIsEnum(result.getIsEnum());
      this.setIsArray(true);
      this.setArrayDepth(result.getArrayDepth());

    } else {
      logger.debug("unknow type");
    }

  }

  public Boolean getIsEnum() {
    return isEnum;
  }

  public void setIsEnum(Boolean anEnum) {
    isEnum = anEnum;
  }

}
