package com.ali.jar2proxy.generic.model;

import java.io.Serializable;

import com.ali.jar2proxy.generic.enums.TOPIC_TYPE;

/**
 * 封装一个用户对主题进行点赞操作的请求
 * 
 * @author tianchi.ya
 * @version $Id: TNewsPopRequest.java,v 0.1 2014年9月18日 上午10:24:47 tianchi.ya Exp $
 */
public class TTopicPopRequest implements  Serializable {

    private static final long serialVersionUID = 1944519209270916615L;
    
    private TOPIC_TYPE topicType;
    private String topicId;
    private String userId;

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TOPIC_TYPE getTopicType() {
        return topicType;
    }

    public void setTopicType(TOPIC_TYPE topicType) {
        this.topicType = topicType;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TTopicPopRequest [topicType=" + topicType + ", topicId=" + topicId + ", userId="
               + userId + "]";
    }
}
