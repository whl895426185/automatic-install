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
        if (deviceName.contains(PhoneTypeConstant.HUAWEI_PHONE_MODEL1)) {
            return true;
        }

        boolean flag = waitAllow(driver);
        if (flag) {
            int alrerAcceptCount = 2;//弹框次数

            //点击【允许】按钮
            if (deviceName.contains(PhoneTypeConstant.OPPO_PHONE)
                    || deviceName.contains(PhoneTypeConstant.MEIZU_PHONE)) {
                alrerAcceptCount = 1;
            }

            for (int i = 0; i < alrerAcceptCount; i++) {
                driver.switchTo().alert().accept();
            }
        }

        return true;
    }

    private boolean waitAllow(AndroidDriver driver) {
        boolean flag = true;
        try {
            //显示等待5秒
            WebDriverWait wait = new WebDriverWait(driver, 5);
            By by = By.xpath("//*//*[contains(@text,'允许')]");

            wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Exception e) {
            flag = false;
        } finally {
            return flag;
        }
    }
}
