package com.wljs.install.step.phone;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 三星手机,兼容机型：
 * 1. Samsung Galaxy S7 edge：型号是SM-G9350
 */
public class Samsung{
    private Logger logger = LoggerFactory.getLogger(Samsung.class);

    public ResponseData installStep(AndroidDriver driver, StfDevicesFields fields) {
        ResponseData responseData = new ResponseData();
        return responseData;
    }
}
