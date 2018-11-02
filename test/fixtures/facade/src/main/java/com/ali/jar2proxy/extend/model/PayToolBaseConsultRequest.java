package com.ali.jar2proxy.extend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author coolme200
 */
public class PayToolBaseConsultRequest<T extends BusinessContext> implements PayToolConsultRequest {

    /** serialVersionUID */
    private static final long serialVersionUID = 6357226815750015030L;
    private String            userId;
    private List<T>           businessContexts;
    private List<String>      assetTypeCodes   = new ArrayList<String>();

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public List<T> getBusinessContexts() {
        return businessContexts;
    }
    public void setBusinessContexts(List<T> businessContexts) {
        this.businessContexts = businessContexts;
    }
    public List<String> getAssetTypeCodes() {
        return assetTypeCodes;
    }
    public void setAssetTypeCodes(List<String> assetTypeCodes) {
        this.assetTypeCodes = assetTypeCodes;
    }

}
