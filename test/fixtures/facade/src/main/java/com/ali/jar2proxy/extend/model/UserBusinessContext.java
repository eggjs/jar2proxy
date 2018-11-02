package com.ali.jar2proxy.extend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author coolme200
 */
public class UserBusinessContext extends BusinessContext {

    /** serialVersionUID */
    private static final long     serialVersionUID = -457440046605016353L;

    private List<PresetAssetInfo> presetAssetInfos = new ArrayList<PresetAssetInfo>();

    private List<String>          assetTypeCodes   = new ArrayList<String>();

    public List<String> getAssetTypeCodes() {
        return assetTypeCodes;
    }

    public void setAssetTypeCodes(List<String> assetTypeCodes) {
        this.assetTypeCodes = assetTypeCodes;
    }

    public List<PresetAssetInfo> getPresetAssetInfos() {
        return presetAssetInfos;
    }

    public void setPresetAssetInfos(List<PresetAssetInfo> presetAssetInfos) {
        this.presetAssetInfos = presetAssetInfos;
    }

}
