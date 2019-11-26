package com.wljs.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseData implements Serializable {
    private boolean status;//true成功， false失败
    private Exception exception;
    private StfDevicesFields fields;
    private String exMsg;
    private String imagePath;

    public ResponseData(){
        this.status = true;
        this.exception = null;
        this.fields = null;
        this.exMsg = null;
        this.imagePath = null;
    }

}
