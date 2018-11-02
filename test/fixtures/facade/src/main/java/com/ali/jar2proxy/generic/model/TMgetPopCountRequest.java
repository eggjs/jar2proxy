package com.ali.jar2proxy.generic.model;

import com.ali.jar2proxy.generic.enums.TOPIC_TYPE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author coolme200
 */
public class TMgetPopCountRequest implements Serializable {

    private static final long              serialVersionUID = -8953537000674822965L;

    private List<Pair<TOPIC_TYPE, String>> typeIdList;
    
    private String userId;

    public List<Pair<TOPIC_TYPE, String>> getTypeIdList() {
        return new ArrayList<Pair<TOPIC_TYPE, String>>(typeIdList);
    }

    public void setTypeIdList(List<Pair<TOPIC_TYPE, String>> typeIdList) {
        if(typeIdList.size() > 100) {
            throw new RuntimeException("list length can't be over 100");
        }
        this.typeIdList = typeIdList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TMgetPopCountRequest [typeIdList=" + typeIdList + ", userId=" + userId + "]";
    }

}
