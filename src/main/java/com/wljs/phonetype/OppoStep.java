package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
import com.wljs.util.constant.InstallStepConstant;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * OPPO手机,兼容机型：
 * 1. OPPO R9s Plus：型号是OPPO R9s Plus
 */
public class OppoStep extends ElementHandle {
    private Logger logger = LoggerFactory.getLogger(OppoStep.class);

    public String installStep(AndroidDriver driver) {
        String exception = null;
        try {
            //允许安装
            exception = waitingElement(driver, InstallStepConstant.oppo_step_1);
            if(null != exception){
                return exception;
            }
            logger.info("-----------------输入手机验证身份密码-----------------");

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

            exception = tap(driver, InstallStepConstant.oppo_step_1, 1);
            if(null != exception){
                return exception;
            }

            logger.info("-----------------准备开始安装步骤-----------------");
            exception = waitingElement(driver, InstallStepConstant.oppo_step_2_txt);
            if(null != exception){
                return exception;
            }
            exception = tap(driver, InstallStepConstant.oppo_step_2, 2);
            if(null != exception){
                return exception;
            }

            exception = waitingElement(driver, InstallStepConstant.oppo_step_3);
            if(null != exception){
                return exception;
            }
            exception = tap(driver, InstallStepConstant.oppo_step_3, 3);
            if(null != exception){
                return exception;
            }

        } catch (Exception e) {
            logger.info("安装过程中异常信息：" + e);
            exception = e.toString();
        } finally {
            return exception;
        }
    }


    /**
     * 获取元素坐标点
     *
     * @param driver
     * @return
     */
    public String tap(AndroidDriver driver, String text, int type) {
        String keyword = null;
        if (1 == type) {
            keyword = "class=\"android.widget.Button\" text=\"" + text + "\"";
        } else if (2 == type) {
            keyword = "resource-id=\"com.android.packageinstaller:id/bottom_button_layout\"";
        } else if (3 == type) {
            keyword = "com.android.packageinstaller:id/bottom_button_one";
        }

        Map<String, Object> totalXYMap = getCoordinates(driver, keyword, text);
        String expection = String.valueOf(totalXYMap.get("expection"));
        if(null != expection){
            return expection;
        }

        int totalX = Integer.valueOf(String.valueOf(totalXYMap.get("totalX")));
        int totalY = Integer.valueOf(String.valueOf(totalXYMap.get("totalY")));

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

        clickCoordinates(driver, x, y, text);

        return null;
    }
}
