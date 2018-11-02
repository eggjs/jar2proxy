package com.ali.jar2proxy.extend.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author coolme200
 */
public class PresetAssetInfo implements Serializable {

    /** serialVersionUID */
    private static final long   serialVersionUID = 5141204807656671429L;
    private String              assetTypeCode;
    private Map<String, String> presetInfo;

    public String getAssetTypeCode() {
        return assetTypeCode;
    }
    public void setAssetTypeCode(String assetTypeCode) {
        this.assetTypeCode = assetTypeCode;
    }
    public Map<String, String> getPresetInfo() {
        return presetInfo;
    }
    public void setPresetInfo(Map<String, String> presetInfo) {
        this.presetInfo = presetInfo;
    }

}
