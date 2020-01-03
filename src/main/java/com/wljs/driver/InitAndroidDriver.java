package com.wljs.driver;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.ExceptionUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InitAndroidDriver {

    public Map<String, Object> initDriver(StfDevicesFields fields, String apkPath) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName()); // 设备名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");// 平台名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());// 系统版本号
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.sibu.futurebazaar");// 包名
        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".ui.SplashAnimActivity");
        capabilities.setCapability(MobileCapabilityType.APP, apkPath);//.apk文件所在的本地绝对路径或者远程路径
        capabilities.setCapability(MobileCapabilityType.UDID, fields.getSerial());// 物理机的id
        capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, fields.getSystemPort());
        capabilities.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, false);
        capabilities.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, false);
        capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
        // Appium是否需要自动安装和启动应用
        capabilities.setCapability("autoLaunch", false);

        String path = "http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub";
        URL url = new URL(path);

        return init(url, capabilities, apkPath);
    }


    private Map<String, Object> init(URL url, DesiredCapabilities capabilities, String apkPath) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        ResponseData responseData = new ResponseData();

        AndroidDriver driver = null;

        //该应用包是无效或者不存在的, 有可能应用包没有上传完，重新再装一下
        String caseMsg = "The application at '" + apkPath + "' does not exist or is not accessible";

        String exceptionMsg = null;//异常信息

        int count = 0;
        boolean retryFlag = true;//是否重装
        do {

            try {
                Thread.sleep(10000);

                driver = new AndroidDriver(url, capabilities);

                //等待应用启动OK
                driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

                exceptionMsg = null;
                responseData = new ResponseData();

            } catch (Exception e) {
                ExceptionUtil exceptionUtil = new ExceptionUtil();
                exceptionMsg = exceptionUtil.exceptionMsg(e);

                responseData = new ResponseData(false, "初始化driver失败", exceptionMsg, e);
            }

            if (null == exceptionMsg || (null != exceptionMsg && !exceptionMsg.contains(caseMsg))) {
                retryFlag = false;
            }
            count++;

        } while (retryFlag && count <= 5);

        resultMap.put("AndroidDriver", driver);
        resultMap.put("responseData", responseData);

        return resultMap;
    }
}
