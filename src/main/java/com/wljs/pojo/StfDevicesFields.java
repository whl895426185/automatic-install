package com.wljs.pojo;


import java.io.Serializable;


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
    //平台
    private String platform;

    /**
     * 自定义扩展字段
     */
    //appium端口
    private int appiumServerPort;
    //系统端口
    private int systemPort;
    //部署异常
    private Exception expection;

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        if (!("").equals(this.manufacturer) && null != this.manufacturer) {
            deviceName = this.manufacturer + " "+ this.model;
        } else {
            deviceName = this.model;
            deviceName = deviceName.substring(1, deviceName.length());
        }
        return deviceName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getAppiumServerPort() {
        return appiumServerPort;
    }

    public void setAppiumServerPort(int appiumServerPort) {
        this.appiumServerPort = appiumServerPort;
    }

    public int getSystemPort() {
        return systemPort;
    }

    public void setSystemPort(int systemPort) {
        this.systemPort = systemPort;
    }

    public Exception getExpection() {
        return expection;
    }

    public void setExpection(Exception expection) {
        this.expection = expection;
    }


}
