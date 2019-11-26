package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.ScreenshotUtil;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 魅族手机,兼容机型：
 * 1. 魅蓝5s：型号是M5s
 */
public class MeizuStep extends ElementHandle {
    private Logger logger = LoggerFactory.getLogger(MeizuStep.class);
    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();

    public ResponseData installStep(AndroidDriver driver, StfDevicesFields fields) {
        ResponseData responseData = new ResponseData();

        logger.info(":::::::::::::::::【" + fields.getDeviceName() + "】::::::::::::::::: 准备开始安装步骤");

        //向左滑動
//        Dimension dimension = driver.manage().window().getSize();
//        int width = dimension.width;
//        int height = dimension.height;
//
//        int orginWith = (new Double(width * 0.9)).intValue();
//        int orginHeight = height / 2;
//        int moveWidth = (new Double(width * 0.15)).intValue();
//        int moveHeight = height / 2;
//
//        new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
//                .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();

       /* responseData = waitingElement(driver, fields, LabelConstant.allowBtnName);
        if (!responseData.isStatus()) {
            return responseData;
        }*/
        try {
            WebDriverWait wait = new WebDriverWait(driver, 60);
            By by = By.xpath("//*//*[@text='" + LabelConstant.allowBtnName + "']");
            wait.until(ExpectedConditions.presenceOfElementLocated(by));

            logger.info(":::::::::::::::::【" + fields.getDeviceName() + "】::::::::::::::::: 等待元素【" + LabelConstant.allowBtnName + "】已出现，开始执行安装步骤");

            driver.switchTo().alert().accept();
            logger.info(":::::::::::::::::【" + fields.getDeviceName() + "】::::::::::::::::: 模拟点击【" + LabelConstant.allowBtnName + "】按钮");

        } catch (Exception e) {
            logger.error(":::::::::::::::::【" + fields.getDeviceName() + "】::::::::::::::::: 没有发现元素【" + LabelConstant.allowBtnName + "】");
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("没有定位到元素： //*//*[@text='" + LabelConstant.allowBtnName + "']");
            responseData.setImagePath(screenshotUtil.screenshot(driver, fields.getSerial()));
        } finally {
            return responseData;
        }
    }
}
