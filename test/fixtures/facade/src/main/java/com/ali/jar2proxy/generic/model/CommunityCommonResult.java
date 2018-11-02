package com.ali.jar2proxy.generic.model;

import java.io.Serializable;

/**
 * @author coolme200
 */
public class CommunityCommonResult implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1974592757576421796L;

    /** success  */
    private boolean           success          = false;

    /** resultCode */
    private String            resultCode;

    /** resultDesc */
    private String            resultDesc;

    /** resultView  */
    private String            resultView;

    public CommunityCommonResult() {
        super();
    }

    /**
     *
     * @param success
     * @param resultDesc
     */
    public CommunityCommonResult(boolean success, String resultDesc) {
        this.success = success;
        this.resultDesc = resultDesc;
    }

    /**
     * Getter method for property <tt>success</tt>.
     *
     * @return property value of success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Setter method for property <tt>success</tt>.
     *
     * @param success value to be assigned to property success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Getter method for property <tt>resultDesc</tt>.
     *
     * @return property value of resultDesc
     */
    public String getResultDesc() {
        return resultDesc;
    }

    /**
     * Setter method for property <tt>resultDesc</tt>.
     *
     * @param resultDesc value to be assigned to property resultDesc
     */
    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    /**
     * Getter method for property <tt>resultCode</tt>.
     *
     * @return property value of resultCode
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Setter method for property <tt>resultCode</tt>.
     *
     * @param resultCode value to be assigned to property resultCode
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Getter method for property <tt>resultView</tt>.
     *
     * @return property value of resultView
     */
    public String getResultView() {
        return resultView;
    }

    /**
     * Setter method for property <tt>resultView</tt>.
     *
     * @param resultView value to be assigned to property resultView
     */
    public void setResultView(String resultView) {
        this.resultView = resultView;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CommonResult [success=" + success + ", resultCode=" + resultCode + ", resultDesc="
               + resultDesc + ", resultView=" + resultView + "]";
    }
}
