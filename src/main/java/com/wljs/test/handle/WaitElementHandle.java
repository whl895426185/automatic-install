package com.wljs.test.handle;

import com.wljs.pojo.Coordinates;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 執行UI自动化测试，元素处理
 */
public class WaitElementHandle {
    private Logger logger = LoggerFactory.getLogger(WaitElementHandle.class);

    /**
     * 显示等待元素出现
     *
     * @param driver
     * @param text
     * @param type
     * @return
     */
    public boolean isAppear(AndroidDriver driver, StfDevicesFields fields, String text, int type) {
        boolean isSuccess = true;
        try {
            WebDriverWait wait = new WebDriverWait(driver, 3);
            By by = null;
            if (1 == type) {
                //根据text定位元素
                by = By.xpath("//*//*[@text='" + text + "']");

            } else if (2 == type) {
                //根据xpath定位元素
                by = By.xpath(text);
            }

            WebElement em = wait.until(ExpectedConditions.presenceOfElementLocated(by));

            //点击其他方式（兼容新版登录）
            if (text.equals(LabelConstant.otherLoginBtnName)) {
                em.click();
            }
            return true;
        } catch (Exception e) {
            logger.info(":::::::::::::::::【" + fields.getDeviceName() + "】没有发现元素【" + text + "】");
            isSuccess = false;
        } finally {
            return isSuccess;
        }
    }

    /**
     * 模拟触摸点击
     *
     * @param driver
     * @param text
     * @param type
     */
    public void tap(AndroidDriver driver, StfDevicesFields fields, String text, int type) {
        Coordinates coordinates = getXy(driver, text, 1);

        int x = coordinates.getTotalX() / 2;
        int y = coordinates.getTotalY() / 2;

        logger.info(":::::::::::::::::【" + fields.getDeviceName() + "】模拟点击【" + text + "】按钮");

        TouchAction t = new TouchAction(driver);//模拟触摸点击
        t.tap(PointOption.point(x, y)).perform().release();
    }

    /**
     * 获取元素坐标
     *
     * @param driver
     * @param text
     * @param type
     * @return
     */
    public Coordinates getXy(AndroidDriver driver, String text, int type) {
        String xmlStr = driver.getPageSource();
        String keyword = null;
        if (0 == type) {
            keyword = "class=\"android.widget.Button\" text=\"" + text + "\"";
        } else if (1 == type) {
            keyword = "class=\"android.widget.TextView\" text=\"" + text + "\"";
        }
        xmlStr = xmlStr.split(keyword)[1];
        xmlStr = xmlStr.split("bounds=\"")[1];
        xmlStr = xmlStr.substring(0, xmlStr.lastIndexOf("]"));
        xmlStr = xmlStr.replace("][", ",").replace("[", "");

        String[] bounsArray = xmlStr.split(",");

        Coordinates coordinates = new Coordinates();
        coordinates.setMinX(Integer.valueOf(bounsArray[0]));
        coordinates.setMinY(Integer.valueOf(bounsArray[1]));
        coordinates.setMaxX(Integer.valueOf(bounsArray[2]));
        coordinates.setMaxY(Integer.valueOf(bounsArray[3]));

        coordinates.setTotalX(coordinates.getMinX() + coordinates.getMaxX());
        coordinates.setTotalY(coordinates.getMinY() + coordinates.getMaxY());
        return coordinates;
    }


}
