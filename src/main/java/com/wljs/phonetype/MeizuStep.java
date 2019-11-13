package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
import com.wljs.pojo.ResponseData;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 魅族手机,兼容机型：
 * 1. 魅蓝5s：型号是M5s
 */
public class MeizuStep extends ElementHandle {
    private Logger logger = LoggerFactory.getLogger(MeizuStep.class);

    public ResponseData installStep(AndroidDriver driver) {
        ResponseData responseData = new ResponseData();

        logger.info("-----------------准备开始安装步骤-----------------");
        responseData = waitingElement(driver, LabelConstant.allowBtnName);
        if (!responseData.isStatus()) {
            return responseData;
        }
        logger.info("-----------------模拟点击【允许】按钮-----------------");
        driver.switchTo().alert().accept();

        return responseData;

    }
}
