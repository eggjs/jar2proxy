package com.ali.jar2proxy.normal.facade;

import com.ali.jar2proxy.normal.model.AbstractUser;
import com.ali.jar2proxy.normal.model.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * normal model interface
 * @author coolme200
 */
public interface UserFacade {

  /**
   * Override method queryByUserId
   * @param userId
   * @return UserInfo
   */
  public UserInfo queryByUserId(String userId);

  /**
   * Override method queryByUserId
   * @param user
   * @return UserInfo
   */
  public UserInfo queryByUserId(UserInfo user);

  /**
   * Param type is String Array
   * @param userIds
   * @return UserInfo
   */
  public UserInfo queryByUserIds(String[] userIds);

  /**
   * Param type is String deep Array
   * @param userIds
   * @return UserInfo
   */
  public UserInfo queryByUserIds(String[][] userIds);

  /**
   * Single parameter
   * @param userId
   * @return String
   */
  public String queryUserNameByUserId(String userId);

  /**
   * Param with generic
   * @param inputProps
   * @return
   */
  public UserInfo queryUserInfoByGeneric(Map<Integer, String[]> inputProps);

  /**
   * Param with generic
   * @param friendNames
   * @return
   */
  public UserInfo queryUserInfoByGeneric(List<String> friendNames);

  /**
   * Param with abstract class
   * @param user
   * @return
   */
  public String queryUserInfoByAbstractObject(AbstractUser user);

  /**
   * Return type is generic List<UserInfo>
   * @param query
   * @return String
   */
  public List<UserInfo> fuzzyQuery(String query, int pageSize, int pageIndex);

  /**
   * Return type is UserInfo Array
   * @param query
   * @return String
   */
  public UserInfo[] fuzzyQueryReturnArray(String query, int pageSize, int pageIndex);

  /**
   * param with primitive type int array
   * @param ids
   * @return String
   */
  public UserInfo[] fuzzyQueryReturnArray(int[] ids);

  /**
   * param with primitive type int
   * @param id
   * @return String
   */
  public UserInfo[] fuzzyQueryReturnArray(int id);

}
