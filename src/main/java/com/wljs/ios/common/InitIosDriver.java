package com.wljs.ios.common;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.config.AppConfig;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class InitIosDriver {

    public DesiredCapabilities setCapabilities(StfDevicesFields fields){
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.UDID, fields.getSerial());// 设备ID
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName()); // 设备名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "IOS");// 平台名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());// 系统版本号
        capabilities.setCapability(MobileCapabilityType.APP, AppConfig.appPackage);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        capabilities.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS,true);
        capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 180);

        return capabilities;
    }


    public IOSDriver initDriver(StfDevicesFields fields, DesiredCapabilities capabilities) throws MalformedURLException {
        String path = "http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub";
        URL url = new URL(path);

        IOSDriver driver = new IOSDriver(url, capabilities);
        driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);

        return driver;
    }
}
