package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 三星手机,兼容机型：
 * 1. Samsung Galaxy S7 edge：型号是SM-G9350
 *
 */
public class SamsungStep extends ElementHandle {
    private Logger logger = LoggerFactory.getLogger(SamsungStep.class);

    public boolean installStep(AndroidDriver driver) {
        boolean isSuccess = true;
        try {

        } catch (Exception e) {
            logger.info("安装过程中异常信息：" + e);
            isSuccess = false;
        } finally {
            return isSuccess;
        }
    }
}
