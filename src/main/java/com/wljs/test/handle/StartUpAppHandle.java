package com.wljs.test.handle;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.ConfigConstant;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 执行UI自动化测试，启动APP
 */
public class StartUpAppHandle {
    private Logger logger = LoggerFactory.getLogger(StartUpAppHandle.class);

    public Map<String, Object> startUpApp(StfDevicesFields fields, String appPath){
        Map<String, Object> resultMap = new HashMap<String, Object>();

        AndroidDriver driver = null;

        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, ConfigConstant.platformName);
        cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());
        cap.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, ConfigConstant.appPackage);
        cap.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ConfigConstant.appActivity);
        cap.setCapability(MobileCapabilityType.APP, appPath);
        cap.setCapability(MobileCapabilityType.UDID, fields.getSerial());
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName());
        cap.setCapability(MobileCapabilityType.NO_RESET, true);
        cap.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, fields.getSystemPort());
        cap.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, false);
        cap.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, false);

        try {
            String path = "http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub";
            URL url =  new URL(path);
            logger.info("---------------初始化driver参数信息---------------");
            driver = new AndroidDriver<AndroidElement>(url, cap);
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        } catch (MalformedURLException e) {
            resultMap.put("expection", e.toString());

        }finally {
            resultMap.put("AndroidDriver", driver);
            return resultMap;
        }
    }
}
