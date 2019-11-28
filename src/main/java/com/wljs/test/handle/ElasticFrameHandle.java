package com.wljs.test.handle;

import com.wljs.util.constant.PhoneTypeConstant;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 执行UI自动化测试，点击弹框
 */
public class ElasticFrameHandle {
    public boolean elasticFrameHandle(AndroidDriver driver, String deviceName) {
        try {
            //显示等待5秒
            WebDriverWait wait = new WebDriverWait(driver, 5);
            By by = By.xpath("//*//*[contains(@text,'允许')]");

            wait.until(ExpectedConditions.presenceOfElementLocated(by));

            int alterAcceptCount = 2;//弹框次数

            //点击【允许】按钮
            if (deviceName.contains(PhoneTypeConstant.OPPO_PHONE)
                    || deviceName.contains(PhoneTypeConstant.MEIZU_PHONE)) {
                alterAcceptCount = 1;
            }

            for (int i = 0; i < alterAcceptCount; i++) {
                driver.switchTo().alert().accept();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return true;
        }
    }

}
