package com.ali.jar2proxy.generic.model;

import java.io.Serializable;

import com.ali.jar2proxy.generic.enums.TOPIC_TYPE;

/**
 * @author coolme200
 */
public class TTopicTopRequest implements  Serializable {

    private static final long serialVersionUID = -7508067071080300417L;
    
    private TOPIC_TYPE topicType;

    public TOPIC_TYPE getTopicType() {
        return topicType;
    }

    public void setTopicType(TOPIC_TYPE topicType) {
        this.topicType = topicType;
    }

}
