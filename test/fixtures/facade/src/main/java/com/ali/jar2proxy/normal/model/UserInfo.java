package com.ali.jar2proxy.normal.model;

import java.util.List;
import java.util.Map;

/**
 * @author coolme200
 */
public class UserInfo extends SuperClassField implements java.io.Serializable {

  private Long userId;
  private String userName;
  private String nickName;
  private Integer age;
  // generic & array
  private Map<Integer, String[]> inputProps;
  // generic
  private List<String> friendNames;
  // deep array
  private String[][] deepArray;
  // normal array
  private String[] normalArray;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public Map<Integer, String[]> getInputProps() {
    return inputProps;
  }

  public void setInputProps(Map<Integer, String[]> inputProps) {
    this.inputProps = inputProps;
  }

  public List<String> getFriendNames() {
    return friendNames;
  }

  public void setFriendNames(List<String> friendNames) {
    this.friendNames = friendNames;
  }

  public String[][] getDeepArray() {
    return deepArray;
  }

  public void setDeepArray(String[][] deepArray) {
    this.deepArray = deepArray;
  }

  public String[] getNormalArray() {
    return normalArray;
  }

  public void setNormalArray(String[] normalArray) {
    this.normalArray = normalArray;
  }
}
