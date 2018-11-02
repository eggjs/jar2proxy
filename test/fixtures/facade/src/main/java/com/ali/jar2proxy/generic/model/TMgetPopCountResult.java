package com.ali.jar2proxy.generic.model;

import java.util.List;

/**
 * 批量获取主题下的点赞数的结果封装
 * 
 * @author tianchi.ya
 * @version $Id: TMgetPopCountResult.java,v 0.1 2014年9月22日 上午11:14:54 tianchi.ya Exp $
 */
public class TMgetPopCountResult extends CommunityCommonResult {

    private static final long serialVersionUID = 1429391247769226506L;

    /**
     * 点赞数的结果，其顺序和request里的list顺序一致
     */
    private List<Integer> countList;
    
    private List<Boolean> isPopedList;
    
    private List<Integer> topicCommentCount;

    public List<Integer> getCountList() {
        return countList;
    }

    public void setCountList(List<Integer> countList) {
        this.countList = countList;
    }

    public List<Boolean> getIsPopedList() {
        return isPopedList;
    }

    public void setIsPopedList(List<Boolean> isPopedList) {
        this.isPopedList = isPopedList;
    }

    public List<Integer> getTopicCommentCount() {
        return topicCommentCount;
    }

    public void setTopicCommentCount(List<Integer> topicCommentCount) {
        this.topicCommentCount = topicCommentCount;
    }

    @Override
    public String toString() {
        return "TMgetPopCountResult [countList=" + countList + ", isPopedList=" + isPopedList
               + ", topicCommentCount=" + topicCommentCount + "]";
    }

}
