package com.wljs.pojo;

import java.io.Serializable;


public class ResponseData implements Serializable {
    private boolean status;//true成功， false失败
    private Exception exception;
    private StfDevicesFields fields;
    private String exMsg;
    private String imagePath;
    private String adbExceptionMsg;

    public ResponseData(){
        this.status = true;
        this.exception = null;
        this.fields = null;
        this.exMsg = null;
        this.imagePath = null;
        this.adbExceptionMsg = null;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public StfDevicesFields getFields() {
        return fields;
    }

    public void setFields(StfDevicesFields fields) {
        this.fields = fields;
    }

    public String getExMsg() {
        return exMsg;
    }

    public void setExMsg(String exMsg) {
        this.exMsg = exMsg;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAdbExceptionMsg() {
        return adbExceptionMsg;
    }

    public void setAdbExceptionMsg(String adbExceptionMsg) {
        this.adbExceptionMsg = adbExceptionMsg;
    }
}
