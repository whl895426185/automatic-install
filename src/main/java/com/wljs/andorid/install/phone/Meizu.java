package com.wljs.andorid.install.phone;

import com.wljs.andorid.common.LocationElement;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 魅族手机,兼容机型：
 * 1. 魅蓝5s：型号是M5s
 */
public class Meizu {
    private Logger logger = LoggerFactory.getLogger(Meizu.class);
    private LocationElement locationElement = new LocationElement();
    private ResponseData responseData = new ResponseData();

    public ResponseData installStep(AndroidDriver driver, StfDevicesFields fields) {

        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 准备开始安装步骤");

        //获取允许按钮
        String allowBtnName = LocationElement.allowBtnName;

        responseData = locationElement.isAppearByText(driver, fields, allowBtnName);
        if (responseData.isStatus()) {
            responseData.getWebElement().click();


            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【" + allowBtnName + "】按钮");
        }
        return responseData;
    }
}
