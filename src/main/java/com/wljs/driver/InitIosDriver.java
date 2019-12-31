package com.wljs.driver;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.CommandUtil;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class InitIosDriver {
    private CommandUtil commandUtil = new CommandUtil();

    public IOSDriver initDriver(StfDevicesFields fields) {
        IOSDriver driver = null;
        try {
            //检查端口是否被占用,如果被占用先停掉
            commandUtil.stopProcess(fields);

            DesiredCapabilities cap = new DesiredCapabilities();
            cap.setCapability(MobileCapabilityType.UDID, fields.getSerial());// 设备ID
            cap.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName()); // 设备名称
            cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");// 平台名称
            cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());// 系统版本号
            cap.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.sibu.futurebazaar");
            cap.setCapability(MobileCapabilityType.NO_RESET, true);
            cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
            cap.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS, true);
            //Mac 主机就会使用这个端口，通过 USB 发送数据到 iOS 设备中
            cap.setCapability(IOSMobileCapabilityType.WDA_LOCAL_PORT, fields.getWdaLocalPort());
            //如果为true，则强制卸载设备上任何现有的WebDriverAgent应用程序。true如果要WebDriverAgent为每个会话应用不同的启动选项，
            cap.setCapability(IOSMobileCapabilityType.USE_NEW_WDA, false);
            //表示开启ios-webkit-debug-proxy工具
            cap.setCapability(IOSMobileCapabilityType.START_IWDP, true);
            //跳过运行WDA应用程序的构建阶段
            cap.setCapability(IOSMobileCapabilityType.USE_PREBUILT_WDA, false);

            String path = "http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub";

            driver = new IOSDriver<IOSElement>(new URL(path), cap);
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            return driver;
        }

    }
}
