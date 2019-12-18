package com.wljs.andorid.uiautomation.handle;

import com.wljs.andorid.common.LocationElement;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.PhoneNumUtil;
import com.wljs.util.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行UI自动化测试，模拟登录
 */
public class AndroidLoginHandle {
    private Logger logger = LoggerFactory.getLogger(AndroidLoginHandle.class);
    //截图工具类
    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();
    //定位元素
    private LocationElement locationElement = new LocationElement();

    private ResponseData responseData = new ResponseData();


    public ResponseData login(AndroidDriver driver, StfDevicesFields fields, int phoneTailNumber, int i) {
        try {
            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getResourceId("tv_getCode"));
            if (responseData.isStatus()) {
                //填写手机号
                WebElement phoneEle = driver.findElement(By.xpath(locationElement.getResourceId("et_phone")));
                phoneEle.click();
                pressKeyPhone(driver, fields, phoneTailNumber);

                //填写验证码
                WebElement inviteCodeEle = driver.findElement(By.xpath(locationElement.getResourceId("et_code")));
                inviteCodeEle.click();
                pressKeyVerificationCode(driver, fields);

                boolean isSuccess = true;
                try {
                    driver.hideKeyboard();
                } catch (Exception e) {
                    isSuccess = false;
                } finally {
                    isSuccess = true;
                }

                //点击【登录】按钮
                responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getResourceId("bt_login"));
                if (responseData.isStatus()) {
                    responseData.getWebElement().click();
                    logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【登录】按钮");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!responseData.isStatus()) {
                if (2 == i) {//第二次登陆不成功
                    //登陆不成功，则执行自动化UI失败
                    String screenImg = screenshotUtil.screenshot(driver, null, fields.getSerial());

                    responseData.setImagePath(screenImg);
                }
            }
            return responseData;
        }
    }

    /**
     * 填写手机号
     *
     * @param driver
     */
    private void pressKeyPhone(AndroidDriver driver, StfDevicesFields fields, int phoneTailNumber) {
        PhoneNumUtil phoneNumUtil = new PhoneNumUtil();
        String[] phoneArray = phoneNumUtil.getPhoneArray(phoneTailNumber);

        String result = "";
        for (String str : phoneArray) {
            if (str.equals("|")) {
                continue;
            }
            driver.pressKey(new KeyEvent(getKey(str)));
            result += str;
        }
        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟输入手机号 = " + result);
    }

    private AndroidKey getKey(String numStr) {
        int result = Integer.valueOf(numStr);
        if (0 == result) {
            return AndroidKey.DIGIT_0;
        } else if (1 == result) {
            return AndroidKey.DIGIT_1;
        } else if (2 == result) {
            return AndroidKey.DIGIT_2;
        } else if (3 == result) {
            return AndroidKey.DIGIT_3;
        } else if (4 == result) {
            return AndroidKey.DIGIT_4;
        } else if (5 == result) {
            return AndroidKey.DIGIT_5;
        } else if (6 == result) {
            return AndroidKey.DIGIT_6;
        } else if (7 == result) {
            return AndroidKey.DIGIT_7;
        } else if (8 == result) {
            return AndroidKey.DIGIT_8;
        } else if (9 == result) {
            return AndroidKey.DIGIT_9;
        }
        return null;
    }


    /**
     * 填写验证码
     *
     * @param driver
     */
    private void pressKeyVerificationCode(AndroidDriver driver, StfDevicesFields fields) {
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟输入验证码 = 888888");
    }

    //新账号需要填写邀请码
    public void inviteCode(AndroidDriver driver, StfDevicesFields fields) {
        responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getResourceId("ll_mine"));
        if (responseData.isStatus()) {
            return;
        }
        //填写邀请码
        WebElement inviteCodeEle = driver.findElement(By.xpath(locationElement.getResourceId("et_invite_code")));
        inviteCodeEle.click();
        pressKeyInvitationCode(driver, fields);

        //输入微信号码
        WebElement wxEle = driver.findElement(By.xpath(locationElement.getResourceId("et_wechat")));
        wxEle.click();
        pressKeyWxCode(driver, fields);

        //将微信号展示给邀请人
        responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getResourceId("wechat_check"));
        if (responseData.isStatus()) {
            responseData.getWebElement().click();
        }

        //同意协议并注册
        responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getResourceId("bt_login"));
        if (responseData.isStatus()) {
            responseData.getWebElement().click();

            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【同意协议并注册】按钮");
        }
    }

    /**
     * 输入微信号码:123456(绑定账号13600000001)
     *
     * @param driver
     * @param fields
     */
    private void pressKeyWxCode(AndroidDriver driver, StfDevicesFields fields) {
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_1));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_2));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_3));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_4));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_5));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_6));
        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟输入微信号码 = 123456");
    }

    /**
     * 填写邀请码:7996137(绑定账号13600000001)
     *
     * @param driver
     */
    private void pressKeyInvitationCode(AndroidDriver driver, StfDevicesFields fields) {
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_7));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_9));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_9));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_6));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_1));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_3));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_7));
        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟输入邀请码 = 7996137");
    }

}
