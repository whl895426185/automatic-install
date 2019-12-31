package com.wljs.install.step;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取元素的标识定义
 */
public class PositionedElements {
    private Logger logger = LoggerFactory.getLogger(PositionedElements.class);

    //执行安装
    public String getText(String text) {
        return "//*//*[@text='" + text + "']";
    }

    //弹框
    public static final String allowBtnName = "允许";


    private ResponseData waitElement(AndroidDriver driver, StfDevicesFields fields, String xpath) {
        ResponseData responseData = new ResponseData();
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            By by = MobileBy.xpath(xpath);


            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));

            responseData.setWebElement(element);

        } catch (Exception e) {
            //e.printStackTrace();
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 没有发现元素【" + xpath + "】");

            responseData = new ResponseData(false, e, "没有获取到元素：" + xpath);

        } finally {
            responseData.setFields(fields);
            return responseData;
        }
    }

    /**
     * 显示等待元素出现
     *
     * @param driver
     * @param text
     * @return
     */
    public ResponseData isAppearByText(AndroidDriver driver, StfDevicesFields fields, String text) {
        String xpath = "//*//*[@text='" + text + "']";

        return waitElement(driver, fields, xpath);

    }

}
