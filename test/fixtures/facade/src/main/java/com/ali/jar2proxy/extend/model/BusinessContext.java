package com.ali.jar2proxy.extend.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author coolme200
 */
public class BusinessContext implements Serializable {

    /** serialVersionUID */
    private static final long   serialVersionUID = 3447256887095088838L;
    private String              payeeUserId;
    private Map<String, String> extraParams;

    public String getPayeeUserId() {
        return payeeUserId;
    }
    public void setPayeeUserId(String payeeUserId) {
        this.payeeUserId = payeeUserId;
    }
    public Map<String, String> getExtraParams() {
        return extraParams;
    }
    public void setExtraParams(Map<String, String> extraParams) {
        this.extraParams = extraParams;
    }

}
