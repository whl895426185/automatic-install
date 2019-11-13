package com.wljs.phonetype.handle;

import com.wljs.pojo.Coordinates;
import com.wljs.pojo.ResponseData;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ElementHandle {
    private Logger logger = LoggerFactory.getLogger(ElementHandle.class);

    /**
     * 显示等待元素出现
     *
     * @param driver
     * @param text
     * @return
     */
    public ResponseData waitingElement(AndroidDriver driver, String text) {
        ResponseData responseData = new ResponseData();
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            By by = By.xpath("//*//*[@text='" + text + "']");
            wait.until(ExpectedConditions.presenceOfElementLocated(by));

            logger.info("-----------------等待元素【" + text + "】已出现，开始执行安装步骤-----------------");
        } catch (Exception e) {
            logger.info("---------------没有发现元素【" + text + "】---------------");
            responseData.setStatus(false);
            responseData.setException(e);
        } finally {
            return responseData;
        }
    }

    /**
     * 获取元素坐标
     *
     * @param driver
     * @param keyword 关键字
     * @param text    安装步骤名称
     * @return
     */
    public Coordinates getCoordinates(AndroidDriver driver, String keyword, String text) {
        Coordinates coordinates = new Coordinates();
        String xmlStr = driver.getPageSource();

        if (!xmlStr.contains(text)) {
            return null;
        }
        xmlStr = xmlStr.split(keyword)[1];
        xmlStr = xmlStr.split("bounds=\"")[1];
        xmlStr = xmlStr.substring(0, xmlStr.lastIndexOf("]"));
        xmlStr = xmlStr.replace("][", ",").replace("[", "");

        String[] bounsArray = xmlStr.split(",");

        coordinates.setMinX(Integer.valueOf(bounsArray[0]));
        coordinates.setMinY(Integer.valueOf(bounsArray[1]));
        coordinates.setMaxX(Integer.valueOf(bounsArray[2]));
        coordinates.setMaxY(Integer.valueOf(bounsArray[3]));

        coordinates.setTotalX(coordinates.getMinX() + coordinates.getMaxX());
        coordinates.setTotalY(coordinates.getMinY() + coordinates.getMaxY());

        return coordinates;
    }

    /**
     * 点击坐标
     *
     * @param driver
     * @param x      横坐標
     * @param y      纵坐标
     * @param text   安装步骤名称
     */
    public void clickCoordinates(AndroidDriver driver, double x, double y, String text) {
        int resultX = new Double(x).intValue();
        int resultY = new Double(y).intValue();

        logger.info("-----------------X坐标为：" + resultX + ", Y坐标为：" + resultY + "-----------------");
        logger.info("-----------------模拟点击元素【" + text + "】按钮-----------------");

        TouchAction t = new TouchAction(driver);//模拟触摸点击
        t.tap(PointOption.point(resultX, resultY)).perform().release();
    }
}
