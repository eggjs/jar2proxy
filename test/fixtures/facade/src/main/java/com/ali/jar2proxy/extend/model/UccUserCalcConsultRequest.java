package com.ali.jar2proxy.extend.model;

import java.util.List;

/**
 * @author coolme200
 */
public class UccUserCalcConsultRequest extends UserConsultRequest {

    /** serial */
    private static final long serialVersionUID = -5147513775306784936L;
    private List<String>      templateIds;
    private String            consultScence;

    public List<String> getTemplateIds() {
        return templateIds;
    }
    public void setTemplateIds(final List<String> templateIds) {
        this.templateIds = templateIds;
    }
    public String getConsultScence() {
        return consultScence;
    }
    public void setConsultScence(final String consultScence) {
        this.consultScence = consultScence;
    }
}
