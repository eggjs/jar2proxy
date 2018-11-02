package com.ali.jar2proxy.generic.facade;

import com.ali.jar2proxy.generic.model.CommunityCommonResult;
import com.ali.jar2proxy.generic.model.TMgetPopCountRequest;
import com.ali.jar2proxy.generic.model.TMgetPopCountResult;
import com.ali.jar2proxy.generic.model.TTopicPopRequest;

public interface TopicConcernFacade {

    /**
     * @param request
     * @return
     */
    public CommunityCommonResult unpopOneTopic(TTopicPopRequest request);

    /**
     * @param request
     * @return
     */
    public CommunityCommonResult popOneTopic(TTopicPopRequest request);

    /**
     * @param request
     * @return
     */
    public TMgetPopCountResult mgetPopCountList(TMgetPopCountRequest request);

}
