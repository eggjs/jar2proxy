package com.ali.jar2proxy.astparser.model.description.base;

import com.ali.jar2proxy.astparser.AstParser;

public abstract class TypeDescription {

  protected String canonicalName;
  protected String commentText;

  public void parseClassAnnotation(Object doc) {

  }

//  public void parseMethodAnnotation(Object doc) {
//
//  }

  /**
   * @param doc
   */
  public abstract void parse(Object doc) throws Exception;

  public boolean ifTypeEnum() {
    return AstParser.threadLocal.get().getOutput().getEnumMap().containsKey(this.getCanonicalName());
  }

  public String getCanonicalName() {
    return canonicalName;
  }

  public void setCanonicalName(String canonicalName) {
    this.canonicalName = canonicalName;
  }

  public String getCommentText() {
    return commentText;
  }

  public void setCommentText(String commentText) {
    this.commentText = commentText;
  }

}
