package com.wljs.test.handle.ios;

import com.wljs.pojo.Coordinates;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.test.handle.CoordinatesHandle;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 執行UI自动化测试，元素处理
 */
public class WaitElementHandle {
    private Logger logger = LoggerFactory.getLogger(WaitElementHandle.class);

    private CoordinatesHandle coordinatesHandle = new CoordinatesHandle();

    /**
     * 显示等待元素出现
     *
     * @param driver
     * @param text
     * @param type
     * @return
     */
    public ResponseData isAppear(IOSDriver driver, StfDevicesFields fields, String text, int type) {
        ResponseData responseData = new ResponseData();
        try {
            WebDriverWait wait = new WebDriverWait(driver, 2);
            By by = null;
            if (1 == type) {
                //根据text定位元素
                by = By.xpath("//*//*[@text='" + text + "']");

            } else if (2 == type) {
                //根据xpath定位元素
                by = By.xpath(text);
            }

            wait.until(ExpectedConditions.presenceOfElementLocated(by));

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 没有发现元素【" + text + "】");
            responseData.setStatus(false);
            responseData.setFields(fields);
            responseData.setException(e);
            if (1 == type) {
                responseData.setExMsg("没有获取到元素： //*//*[@text='"+ text + "']");

            } else if (2 == type) {
                responseData.setExMsg("没有获取到元素：" + text);
            }
        } finally {
            return responseData;
        }
    }

    /**
     * 模拟触摸点击
     *
     * @param driver
     * @param text
     * @param type
     */
    public void tap(IOSDriver driver, StfDevicesFields fields, String text, int type) {
        Coordinates coordinates = coordinatesHandle.getXy(driver.getPageSource(), text, 1);

        int x = coordinates.getTotalX() / 2;
        int y = coordinates.getTotalY() / 2;

        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【" + text + "】按钮");

        TouchAction t = new TouchAction(driver);//模拟触摸点击
        t.tap(PointOption.point(x, y)).perform().release();
    }




}
