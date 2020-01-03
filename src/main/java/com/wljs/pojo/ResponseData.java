package com.wljs.pojo;

import org.openqa.selenium.WebElement;

import java.io.Serializable;


public class ResponseData implements Serializable {

    private StfDevicesFields fields;//设备信息
    private WebElement webElement;//元素对象
    private boolean status;    //成功状态（true成功，false失败）
    private String imagePath;//异常截图
    private String exceptionTitle;//自定义异常简短说明
    private String exceptionMsg;//自定义异常信息
    private Exception exception;//异常信息对象


    public ResponseData() {
        this.status = true;
        this.exceptionMsg = null;
        this.fields = null;
        this.imagePath = null;
        this.exceptionTitle = null;
        this.webElement = null;
        this.exception = null;
    }

    public ResponseData(boolean status, String exceptionTitle, String exceptionMsg, Exception exception) {
        this.status = status;
        this.exceptionTitle = exceptionTitle;
        this.exceptionMsg = exceptionMsg;
        this.exception = exception;

    }

    public ResponseData(boolean status, String exceptionTitle, String exceptionMsg, String imagePath, Exception exception) {
        this.status = status;
        this.exceptionTitle = exceptionTitle;
        this.exceptionMsg = exceptionMsg;
        this.imagePath = imagePath;
        this.exception = exception;

    }


    public StfDevicesFields getFields() {
        return fields;
    }

    public void setFields(StfDevicesFields fields) {
        this.fields = fields;
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getExceptionTitle() {
        return exceptionTitle;
    }

    public void setExceptionTitle(String exceptionTitle) {
        this.exceptionTitle = exceptionTitle;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
