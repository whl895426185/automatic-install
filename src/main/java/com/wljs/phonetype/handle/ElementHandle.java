package com.wljs.phonetype.handle;

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
    public boolean waitingElement(AndroidDriver driver, String text) {
        boolean isSuccess = true;
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            By by = By.xpath("//*//*[@text='" + text + "']");
            wait.until(ExpectedConditions.presenceOfElementLocated(by));

            logger.info("-----------------等待元素【" + text + "】已出现，开始执行安装步骤-----------------");

            return true;
        } catch (Exception e) {
            logger.info("---------------没有发现元素【" + text + "】---------------");
            isSuccess = false;
        } finally {
            return isSuccess;
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
    public Map<String, Object> getCoordinates(AndroidDriver driver, String keyword, String text) {
        String xmlStr = driver.getPageSource();
        try {
            if (!xmlStr.contains(text)) {
                return null;
            }
            xmlStr = xmlStr.split(keyword)[1];
            xmlStr = xmlStr.split("bounds=\"")[1];
            xmlStr = xmlStr.substring(0, xmlStr.lastIndexOf("]"));
            xmlStr = xmlStr.replace("][", ",").replace("[", "");

            String[] bounsArray = xmlStr.split(",");

            int minX = Integer.valueOf(bounsArray[0]);
            int minY = Integer.valueOf(bounsArray[1]);
            int maxX = Integer.valueOf(bounsArray[2]);
            int maxY = Integer.valueOf(bounsArray[3]);

            Map<String, Object> totalXYMap = new HashMap<String, Object>();

            int totalX = minX + maxX;
            int totalY = minY + maxY;

            totalXYMap.put("totalX", totalX);
            totalXYMap.put("totalY", totalY);
            return totalXYMap;

        } catch (Exception e) {
            logger.info(xmlStr);
            logger.error("执行UI自动化测试失败，异常信息为：" + xmlStr);
        }
        return null;
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
