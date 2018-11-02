package com.ali.jar2proxy.extend.model;

import java.io.Serializable;

/**
 * @author coolme200
 */
public class UccBaseResult implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -8285700606302634484L;
    protected boolean         success          = false;
    public UccBaseResult() { }

    public UccBaseResult(final boolean success) {
        this.success = success;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(final boolean success) {
        this.success = success;
    }

}
