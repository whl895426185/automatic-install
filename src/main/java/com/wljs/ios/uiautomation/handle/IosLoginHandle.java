package com.wljs.ios.uiautomation.handle;

import com.wljs.config.AppConfig;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.PhoneNumUtil;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行UI自动化测试，模拟登录
 */
public class IosLoginHandle {
    private Logger logger = LoggerFactory.getLogger(IosLoginHandle.class);

    private LocationElement locationElement = new LocationElement();

    private ResponseData responseData = new ResponseData();

    public void login(IOSDriver driver, StfDevicesFields fields, int phoneTailNumber) {
        try {
            responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeStaticText("获取验证码"));
            if (responseData.isStatus()) {
                //填写手机号
                WebElement phoneEle = driver.findElementByIosNsPredicate(locationElement.getXCUIElementTypeTextField("请输入手机号"));
                phoneEle.click();
                pressKeyPhone(phoneEle, fields, phoneTailNumber);


                //填写验证码
                WebElement verCodeEle = driver.findElementByIosNsPredicate(locationElement.getXCUIElementTypeTextField("请输入验证码"));
                verCodeEle.click();
                verCodeEle.sendKeys("888888");
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟输入验证码 = 888888");

                //处理键盘
                responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeButton("Done"));
                if (responseData.isStatus()) {
                    responseData.getWebElement().click();
                }

                //点击【登录】按钮
                responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeStaticText("登录"));
                if (responseData.isStatus()) {
                    responseData.getWebElement().click();
                    logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【登录】按钮");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 填写手机号
     *
     * @param element
     * @param fields
     * @param phoneTailNumber
     */
    private void pressKeyPhone(WebElement element, StfDevicesFields fields, int phoneTailNumber) {
        PhoneNumUtil phoneNumUtil = new PhoneNumUtil();
        String[] phoneArray = phoneNumUtil.getPhoneArray(phoneTailNumber);

        String result = "";
        for (String str : phoneArray) {
            if (str.equals("|")) {
                continue;
            }
            result += str;
        }
        element.sendKeys(result);
        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟输入手机号 = " + result);
    }


    /**
     * 新账号需要填写邀请码
     *
     * @param driver
     * @param fields
     */
    public void inviteCode(IOSDriver driver, StfDevicesFields fields) {
        responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeButton("我的"));
        if (responseData.isStatus()) {
            return;

        }
        //填写邀请码
        WebElement inviteCodeEle = driver.findElementByIosNsPredicate(locationElement.getXCUIElementTypeTextField("请输入邀请码"));
        inviteCodeEle.click();
        inviteCodeEle.sendKeys("7996137");//填写邀请码:7996137(绑定账号13600000001)
        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟输入邀请码 = 7996137");

        //输入微信号码
        WebElement wxEle = driver.findElementByIosNsPredicate(locationElement.getXCUIElementTypeButton("ic product pay unselected"));
        wxEle.click();
        wxEle.sendKeys("123456");//输入微信号码:12346(绑定账号13600000001)
        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟输入微信号码 = 123456");

        //将微信号展示给邀请人
        responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeTextField("请输入微信号码"));
        if (responseData.isStatus()) {
            responseData.getWebElement().click();
        }

        //同意协议并注册
        responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeStaticText("同意协议并注册"));
        if (responseData.isStatus()) {
            responseData.getWebElement().click();

            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【同意协议并注册】按钮");
        }
    }


}
