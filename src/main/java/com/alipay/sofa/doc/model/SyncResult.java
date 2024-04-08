package com.alipay.sofa.doc.model;

/**
 * ></a>
 */
public class SyncResult {

    private boolean success;
    private String message;

    public SyncResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public SyncResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public SyncResult setMessage(String message) {
        this.message = message;
        return this;
    }
}
