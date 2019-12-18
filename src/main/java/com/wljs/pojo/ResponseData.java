package com.wljs.pojo;

import org.openqa.selenium.WebElement;

import java.io.Serializable;


public class ResponseData implements Serializable {
    //成功状态（true成功，false失败）
    private boolean status;
    //异常
    private Exception exception;
    //设备信息
    private StfDevicesFields fields;
    //自定义异常信息
    private String exMsg;
    //异常截图
    private String imagePath;
    //adb命令执行异常信息
    private String adbExceptionMsg;
    //元素对象
    private WebElement webElement;

    public ResponseData(){
        this.status = true;
        this.exception = null;
        this.fields = null;
        this.exMsg = null;
        this.imagePath = null;
        this.adbExceptionMsg = null;
        this.webElement = null;
    }

    public ResponseData(boolean status, Exception exception, String exMsg){
        this.status = status;
        this.exception = exception;
        this.exMsg = exMsg;

    }

    public ResponseData(boolean status, Exception exception,String exMsg, String imagePath){
        this.status = status;
        this.exception = exception;
        this.exMsg = exMsg;
        this.imagePath = imagePath;

    }

    public ResponseData(boolean status, Exception exception, String exMsg, String imagePath, String adbExceptionMsg){
        this.status = status;
        this.exception = exception;
        this.exMsg = exMsg;
        this.imagePath = imagePath;
        this.adbExceptionMsg = adbExceptionMsg;

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

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }
}
