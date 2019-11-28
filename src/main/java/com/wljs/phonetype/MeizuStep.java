package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.ScreenshotUtil;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 魅族手机,兼容机型：
 * 1. 魅蓝5s：型号是M5s
 */
public class MeizuStep extends ElementHandle {
    private Logger logger = LoggerFactory.getLogger(MeizuStep.class);
    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();

    public ResponseData installStep(AndroidDriver driver, StfDevicesFields fields) {
        ResponseData responseData = new ResponseData();

        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 准备开始安装步骤");

        try {
            WebDriverWait wait = new WebDriverWait(driver, 60);
            By by = By.xpath("//*//*[@text='" + LabelConstant.allowBtnName + "']");
            wait.until(ExpectedConditions.presenceOfElementLocated(by));

            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 等待元素【" + LabelConstant.allowBtnName + "】已出现，开始执行安装步骤");

            driver.switchTo().alert().accept();
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【" + LabelConstant.allowBtnName + "】按钮");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 没有发现元素【" + LabelConstant.allowBtnName + "】");
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("没有定位到元素： //*//*[@text='" + LabelConstant.allowBtnName + "']");
            responseData.setImagePath(screenshotUtil.screenshot(driver, fields.getSerial()));
        } finally {
            return responseData;
        }
    }
}
