package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
import com.wljs.pojo.Coordinates;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.ScreenshotUtil;
import com.wljs.util.constant.InstallStepConstant;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OPPO手机,兼容机型：
 * 1. OPPO R9s Plus：型号是OPPO R9s Plus
 */
public class OppoStep extends ElementHandle {
    private Logger logger = LoggerFactory.getLogger(OppoStep.class);
    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();

    public ResponseData installStep(AndroidDriver driver, StfDevicesFields fields) {
        ResponseData responseData = new ResponseData();
        //允许安装
        responseData = waitingElement(driver, fields, InstallStepConstant.oppo_step_1);
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

        responseData = tap(driver, fields, InstallStepConstant.oppo_step_1, 1);
        if (!responseData.isStatus()) {
            return responseData;
        }

        logger.info(":::::::::::::::::【" + fields.getDeviceName() + "】::::::::::::::::: 执行安装步骤！！！！");
        responseData = waitingElement(driver, fields, InstallStepConstant.oppo_step_2_txt);
        if (!responseData.isStatus()) {
            return responseData;
        }
        responseData = tap(driver, fields, InstallStepConstant.oppo_step_2, 2);
        if (!responseData.isStatus()) {
            return responseData;
        }

        responseData = waitingElement(driver, fields, InstallStepConstant.oppo_step_3);
        if (!responseData.isStatus()) {
            return responseData;
        }
        responseData = tap(driver, fields, InstallStepConstant.oppo_step_3, 3);
        if (!responseData.isStatus()) {
            return responseData;
        }

        return responseData;
    }


    /**
     * 获取元素坐标点
     *
     * @param driver
     * @return
     */
    public ResponseData tap(AndroidDriver driver, StfDevicesFields fields, String text, int type) {
        ResponseData responseData = new ResponseData();
        String keyword = null;
        try {
            if (1 == type) {
                keyword = "class=\"android.widget.Button\" text=\"" + text + "\"";
            } else if (2 == type) {
                keyword = "resource-id=\"com.android.packageinstaller:id/bottom_button_layout\"";
            } else if (3 == type) {
                keyword = "com.android.packageinstaller:id/bottom_button_one";
            }

            Coordinates coordinates = getCoordinates(driver, keyword, text);

            int totalX = coordinates.getTotalX();
            int totalY = coordinates.getTotalY();

            double x = 0;
            double y = 0;

            if (1 == type) {
                x = totalX / 2;
                y = totalY / 2;

            } else if (2 == type) {
                x = totalX * 0.75;
                y = totalY / 2;

            } else if (3 == type) {
                x = totalX / 2;
                y = totalY * 0.5;
            }

            clickCoordinates(driver, fields, x, y, text);

        } catch (Exception e) {
            logger.error(":::::::::::::::::【" + fields.getDeviceName() + "】::::::::::::::::: 获取坐标信息异常：" + e);
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("无法通过关键字获取坐标信息： " + keyword);
            responseData.setImagePath(screenshotUtil.screenshot(driver, fields.getSerial()));
        } finally {
            return responseData;
        }
    }

}
