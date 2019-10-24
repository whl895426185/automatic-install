package com.wljs.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StfDevicesFields implements Serializable {
    //厂商
    private String manufacturer;
    //设备型号
    private String model;
    //设备UUID
    private String serial;
    //版本号
    private String version;
    //自定义设备名称（厂商 + 型号）
    private String deviceName;

    /**
     * 自定义扩展字段
     */
    //appium端口
    private int appiumServerPort;
    //系统端口
    private int systemPort;
    //安装成功标识
    private boolean resultSuccess;

    public String getDeviceName() {
        if (!("").equals(this.manufacturer) && null != this.manufacturer) {
            deviceName = this.manufacturer + this.model;
        } else {
            deviceName = this.model;
            deviceName = deviceName.substring(1, deviceName.length());
        }
        return deviceName;
    }

}
