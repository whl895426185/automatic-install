package com.wljs.util;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class IosDriverUtil {

    public DesiredCapabilities setCapabilities(StfDevicesFields fields){
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.UDID, fields.getSerial());// 设备ID
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName()); // 设备名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");// 平台名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());// 系统版本号
        capabilities.setCapability(MobileCapabilityType.APP, AppConfig.appPackage);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        capabilities.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS,true);

        return capabilities;
    }


    public IOSDriver<IOSElement> initDriver(StfDevicesFields fields, DesiredCapabilities capabilities) throws MalformedURLException {
        String path = "http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub";
        URL url = new URL(path);

        IOSDriver<IOSElement> driver = new IOSDriver<IOSElement>(url, capabilities);
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        return driver;
    }
}
