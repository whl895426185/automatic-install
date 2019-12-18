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
 * OPPO手机,兼容机型：
 * 1. OPPO R9s Plus：型号是OPPO R9s Plus
 */
public class Oppo extends  CoodinatesTap {
    private Logger logger = LoggerFactory.getLogger(Oppo.class);
    private LocationElement locationElement = new LocationElement();
    private ResponseData responseData = new ResponseData();


    public ResponseData installStep(AndroidDriver driver, StfDevicesFields fields) {
        //允许安装
        responseData = locationElement.isAppearByText(driver, fields, oppo_step_1);
        if (!responseData.isStatus()) {
            return responseData;
        }
        //获取输入框元素，输入密码
        driver.pressKey(new KeyEvent(AndroidKey.Y));
        driver.pressKey(new KeyEvent(AndroidKey.W));
        driver.pressKey(new KeyEvent(AndroidKey.PERIOD));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_6));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_6));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_7));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_1));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_3));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));

        responseData = tap(driver, fields,oppo_step_1, getOppoKeyword1(oppo_step_1));
        if (!responseData.isStatus()) {
            return responseData;
        }

        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行安装步骤！！！！");
        responseData = locationElement.isAppearByText(driver, fields, oppo_step_2_txt);
        if (!responseData.isStatus()) {
            return responseData;
        }
        responseData = tap(driver, fields, oppo_step_2, getOppoKeyword2());
        if (!responseData.isStatus()) {
            return responseData;
        }

        responseData = locationElement.isAppearByText(driver, fields, oppo_step_3);
        if (!responseData.isStatus()) {
            return responseData;
        }
        responseData = tap(driver, fields, oppo_step_3, getOppoKeyword3());
        if (!responseData.isStatus()) {
            return responseData;
        }

        return responseData;
    }


}
