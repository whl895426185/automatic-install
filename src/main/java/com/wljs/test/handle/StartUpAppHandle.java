package com.wljs.test.handle;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.config.AndroidConfig;
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
import java.util.concurrent.TimeUnit;

/**
 * 执行UI自动化测试，启动APP
 */
public class StartUpAppHandle {
    private Logger logger = LoggerFactory.getLogger(StartUpAppHandle.class);

    public AndroidDriver startUpApp(StfDevicesFields fields, String appPath) throws MalformedURLException {
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
        cap.setCapability(MobileCapabilityType.PLATFORM_NAME, AndroidConfig.platformName);
        cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());
        cap.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, AndroidConfig.appPackage);
        cap.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, AndroidConfig.appActivity);
        cap.setCapability(MobileCapabilityType.APP, appPath);
        cap.setCapability(MobileCapabilityType.UDID, fields.getSerial());
        cap.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName());
        cap.setCapability(MobileCapabilityType.NO_RESET, true);
        cap.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, fields.getSystemPort());
        cap.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, false);
        cap.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, false);

        String path = "http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub";
        URL url = new URL(path);

        AndroidDriver driver = new AndroidDriver<AndroidElement>(url, cap);
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        return driver;
    }
}
