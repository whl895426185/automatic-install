package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
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

    public String installStep(AndroidDriver driver) {
        String expection = null;
        try {
            logger.info("-----------------准备开始安装步骤-----------------");
            expection = waitingElement(driver, LabelConstant.allowBtnName);
            if (null != expection) {
                return expection;
            }
            logger.info("-----------------模拟点击【允许】按钮-----------------");
            driver.switchTo().alert().accept();

        } catch (Exception e) {
            logger.info("安装过程中异常信息：" + e);
            expection = e.toString();
        } finally {
            return expection;
        }
    }
}
