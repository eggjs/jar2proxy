package com.ali.jar2proxy.extend.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author coolme200
 */
public class UserConsultInfo implements Serializable {

  private static final long   serialVersionUID  = -7099173192995638436L;
  private boolean             isAllowAddUp;
  private boolean             needPublish       = false;
  private String              detail;
  private Map<String, String> bizContext;

  public boolean isAllowAddUp() {
    return isAllowAddUp;
  }

  public void setAllowAddUp(boolean allowAddUp) {
    isAllowAddUp = allowAddUp;
  }

  public boolean isNeedPublish() {
    return needPublish;
  }

  public void setNeedPublish(boolean needPublish) {
    this.needPublish = needPublish;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public Map<String, String> getBizContext() {
    return bizContext;
  }

  public void setBizContext(Map<String, String> bizContext) {
    this.bizContext = bizContext;
  }
}
