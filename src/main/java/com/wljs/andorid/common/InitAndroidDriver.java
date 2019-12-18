package com.wljs.andorid.common;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.config.AppConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class InitAndroidDriver {

    public DesiredCapabilities setCapabilities(StfDevicesFields fields, String apkPath){
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName()); // 设备名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");// 平台名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());// 系统版本号
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, AppConfig.appPackage);// 包名
        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, AppConfig.appActivity);
        capabilities.setCapability(MobileCapabilityType.APP, apkPath);//.apk文件所在的本地绝对路径或者远程路径
        capabilities.setCapability(MobileCapabilityType.UDID, fields.getSerial());// 物理机的id
        capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, fields.getSystemPort());
        capabilities.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, false);
        capabilities.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, false);
        capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
        return capabilities;
    }


    public AndroidDriver initDriver(StfDevicesFields fields, DesiredCapabilities capabilities) throws MalformedURLException {
        String path = "http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub";
        URL url = new URL(path);

        AndroidDriver driver = new AndroidDriver(url, capabilities);

        //等待应用启动OK
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        return driver;
    }
}
