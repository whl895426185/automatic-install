package com.wljs.server;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.server.selenium.SeleniumLinuxServer;
import com.wljs.server.selenium.SeleniumWindowServer;

import java.util.*;

/**
 * 利用Selenium调用浏览器，动态模拟浏览器事件
 */
public class SeleniumServer {
    private boolean typeFlag = true;//false: window ,true: linux

    /**
     * occupancyResources
     * 占用已连接且闲余的设备
     */
    public List<StfDevicesFields> occupancyResources(List<StfDevicesFields> fieldsList) {
        if (typeFlag) {
            SeleniumLinuxServer linux = new SeleniumLinuxServer();
            return linux.occupancy(fieldsList);
        } else {
            SeleniumWindowServer window = new SeleniumWindowServer();
            return window.occupancy(fieldsList);
        }
    }

    /**
     * 释放资源(每台设备都登录再关闭)
     */
    public void releaseResources(StfDevicesFields fields) {
        if (typeFlag) {
            SeleniumLinuxServer linux = new SeleniumLinuxServer();
            linux.release(fields);
        } else {
            SeleniumWindowServer window = new SeleniumWindowServer();
            window.release(fields);
        }
    }

}
