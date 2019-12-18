package com.wljs.andorid.install.phone;

import com.wljs.andorid.common.LocationElement;
import com.wljs.andorid.install.phone.handle.CoodinatesTap;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VIVO手机,兼容机型：
 * 1. VIVO NEX A：型号是NEX A
 */
public class Vivo extends CoodinatesTap {
    private Logger logger = LoggerFactory.getLogger(Vivo.class);
    private ResponseData responseData = new ResponseData();
    private LocationElement locationElement = new LocationElement();

    public ResponseData installStep(AndroidDriver driver, StfDevicesFields fields) {

        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行安装步骤！！！！");
        if (driver.getPageSource().contains(vivo_step_0)) {
            //获取输入框元素，输入密码（Vickyho0123.）
            driver.pressKey(new KeyEvent(AndroidKey.CAPS_LOCK));
            driver.pressKey(new KeyEvent(AndroidKey.V));
            driver.pressKey(new KeyEvent(AndroidKey.CAPS_LOCK));
            driver.pressKey(new KeyEvent(AndroidKey.I));
            driver.pressKey(new KeyEvent(AndroidKey.C));
            driver.pressKey(new KeyEvent(AndroidKey.K));
            driver.pressKey(new KeyEvent(AndroidKey.Y));
            driver.pressKey(new KeyEvent(AndroidKey.H));
            driver.pressKey(new KeyEvent(AndroidKey.O));
            driver.pressKey(new KeyEvent(AndroidKey.DIGIT_0));
            driver.pressKey(new KeyEvent(AndroidKey.DIGIT_1));
            driver.pressKey(new KeyEvent(AndroidKey.DIGIT_2));
            driver.pressKey(new KeyEvent(AndroidKey.DIGIT_3));
            driver.pressKey(new KeyEvent(AndroidKey.PERIOD));

            responseData = tap(driver, fields, vivo_step_0_1, getVivoKeyword1());
            if (!responseData.isStatus()) {
                return responseData;
            }
        }

        //步驟一
        responseData = locationElement.isAppearByText(driver, fields, vivo_step_1);
        if (!responseData.isStatus()) {
            return responseData;
        }
        responseData = tap(driver, fields, vivo_step_1, getVivoKeyword1());
        if (!responseData.isStatus()) {
            return responseData;
        }

        //步驟二
        responseData = locationElement.isAppearByText(driver, fields, vivo_step_2);
        if (!responseData.isStatus()) {
            return responseData;
        }

        responseData = tap(driver, fields, vivo_step_2, getVivoKeyword2());
        if (!responseData.isStatus()) {
            return responseData;
        }

        //步驟三
        responseData = locationElement.isAppearByText(driver, fields, vivo_step_3);
        if (!responseData.isStatus()) {
            return responseData;
        }
        responseData = tap(driver, fields, vivo_step_3, getVivoKeyword3(fields.getDeviceName()));
        if (!responseData.isStatus()) {
            return responseData;
        }
        return responseData;

    }

}
