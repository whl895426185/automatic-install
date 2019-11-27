package com.wljs.server;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.server.expection.AdbException;
import com.wljs.server.handle.PhoneInstallStepHandle;
import com.wljs.test.handle.ProductHandle;
import com.wljs.util.ScreenshotUtil;
import com.wljs.util.config.AndroidConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 安装APK测试包
 */
public class InstallApkServer {
    private Logger logger = LoggerFactory.getLogger(InstallApkServer.class);
    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();

    /**
     * 外部调用接口，执行部署
     *
     * @param queue
     * @param apkPath
     * @return
     * @throws Exception
     */
    public ResponseData init(ArrayBlockingQueue queue, String apkPath) throws InterruptedException {
        StfDevicesFields fields = (StfDevicesFields) queue.take();

        //启动appium服务
        AppiumServer appiumServer = new AppiumServer();
        appiumServer.start(fields.getAppiumServerPort());

        //执行安装
        return installApp(fields, apkPath);

    }

    /**
     * 安装APP,如有安装，先卸载重装
     *
     * @param fields
     * @return
     * @throws Exception
     */
    public ResponseData installApp(StfDevicesFields fields, String apkPath) {
        ResponseData responseData = new ResponseData();
        AndroidDriver<AndroidElement> driver = null;
        try {
            String device = fields.getSerial();

            //初始化参数信息
            driver = initDriver(fields, apkPath);

            //检查APP是否安装
            if (driver.isAppInstalled(AndroidConfig.appPackage)) {
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 检测到设备之前安装了APP，先执行卸载操作");
                //如果安装了，先卸载
                driver.removeApp(AndroidConfig.appPackage);
            }

            //adb命令执行安装apk（不需要用appium自带的安装函数,它内置方法也是通过adb命令安装的,还得整个安装过程完成，该函数执行才算完成）
            String installCmd = "adb -s " + device + " install " + apkPath;
            Process process = Runtime.getRuntime().exec(installCmd);
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 命令执行安装：" + installCmd);

            boolean isSuccess = true;
            int i = 0;
            do {
                //不同的机型调用不同的安装步骤
                PhoneInstallStepHandle installStepHandle = new PhoneInstallStepHandle();
                responseData = installStepHandle.installStep(fields, driver);
                if (!responseData.isStatus()) {
                    isSuccess = false;
                    if (i < 2) {
                        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 第" + (i + 1) + "次重新尝试安装");
                    }
                }
                i++;
            } while (!isSuccess && i < 3);

            String processMsg = null;
            if (responseData.isStatus()) {
                processMsg = getProcess(process);

                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + "---ADB命令install結果】:::::::::::::::::" + processMsg);

                if (processMsg.contains("Success")) {
                    //禁用检查是否安装成功的方法，执行安装process输出success就证明已经安装成功了，Appium一个应用的session过期时间是60秒，再次检查会导致超时
                    //responseData = installOk(driver, fields, AndroidConfig.appPackage);

                    driver.quit();
                } else {
                    AdbException adb = new AdbException();
                    String adbExeMsg = adb.adbException(processMsg);

                    //ADB安裝包失敗，沒有直接抛出異常，所以手動維護異常
                    responseData.setExMsg("ADB命令执行安装APK包时,报错：" + adbExeMsg);
                    responseData.setAdbExceptionMsg(processMsg);
                    responseData.setStatus(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行自动部署安装失败：" + e);

            responseData.setStatus(false);
            responseData.setException(e);

            if (null == driver) {
                responseData.setExMsg("执行自动部署安装失败： AndroidDriver is null");
            } else {
                responseData.setExMsg("执行自动部署安装失败：" + e);
            }
            responseData.setImagePath(screenshotUtil.screenshot(driver, fields.getSerial()));

        } finally {
            responseData.setFields(fields);
            return responseData;
        }

    }


    /**
     * 初始化AndroidDriver
     *
     * @param fields
     * @return
     */
    private AndroidDriver<AndroidElement> initDriver(StfDevicesFields fields, String apkPath) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName()); // 设备名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, AndroidConfig.platformName);// 平台名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());// 系统版本号
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, AndroidConfig.appPackage);// 包名
        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, AndroidConfig.appActivity);
        capabilities.setCapability(MobileCapabilityType.APP, apkPath);//.ipa or .apk文件所在的本地绝对路径或者远程路径
        capabilities.setCapability(MobileCapabilityType.UDID, fields.getSerial());// 物理机的id
        capabilities.setCapability(AndroidConfig.autoLaunch, false);// Appium是否需要自动安装和启动应用
        capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, fields.getSystemPort());
        capabilities.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, false);
        capabilities.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, false);

        //初始化
        URL url = new URL("http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub");

        AndroidDriver<AndroidElement> driver = new AndroidDriver<AndroidElement>(url, capabilities);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        return driver;
    }

    /**
     * 检查是否安装成功
     *
     * @param driver
     * @param appPackage
     */
    private ResponseData installOk(AndroidDriver driver, StfDevicesFields fields, String appPackage) throws InterruptedException {
        ResponseData responseData = new ResponseData();
        boolean isSuccess = true;
        int i = 0;
        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 准备检查App是否安装成功");
        do {
            Thread.sleep(20000);

            isSuccess = driver.isAppInstalled(appPackage);

            if (!isSuccess && i == 4) {
                responseData.setStatus(false);
                responseData.setExMsg("Appium无法检测到包的安装路径");
            }
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 第" + (i + 1) + "次检查App安装结果：" + (isSuccess ? "成功" : (i == 4 ? "失败" : "安装有点缓慢，请等待！！！！！")) + "");

            i++;
        } while (!isSuccess && i < 5);

        return responseData;
    }


    /**
     * 执行adb命令并返回结果
     *
     * @param process
     * @return
     * @throws Exception
     */
    public String getProcess(Process process) throws Exception {
        InputStream is = process.getInputStream();
        InputStreamReader isReader = new InputStreamReader(is, "GBK");
        BufferedReader br = new BufferedReader(isReader);
        String line = null;
        StringBuffer b = new StringBuffer();
        while ((line = br.readLine()) != null) {
            if (process.waitFor() == 0) {
                b.append(line + "\n");
            }
        }
        return b.toString();
    }


    public static void main(String[] arg) throws Exception {
        StfDevicesFields fields = new StfDevicesFields();
//        fields.setSerial("8KE5T19711012159");
//        fields.setVersion("9.0");
//        fields.setModel(" P30");
//        fields.setManufacturer("HUAWEI");

        fields.setManufacturer("VIVO");
        fields.setModel("X9");
        fields.setSerial("1d4bc416");
        fields.setVersion("7.1.2");

//        fields.setManufacturer("");
//        fields.setModel(" M5s");
//        fields.setSerial("612QKBQJ226WJ");
//        fields.setVersion("6.0");

        fields.setAppiumServerPort(4724);
        fields.setSystemPort(8201);

        //启动appium服务
        AppiumServer appiumServer = new AppiumServer();
        appiumServer.start(fields.getAppiumServerPort());

        //执行安装
        AndroidDriver<AndroidElement> driver = null;

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName()); // 设备名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, AndroidConfig.platformName);// 平台名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());// 系统版本号
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, AndroidConfig.appPackage);// 包名
        capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".ui.SplashActivityNew");
        capabilities.setCapability(MobileCapabilityType.APP, "D:\\apkPackage\\wljs01\\apk\\vc-57-vn-1.8.0-11-26-17-54.apk");//.ipa or .apk文件所在的本地绝对路径或者远程路径
        capabilities.setCapability(MobileCapabilityType.UDID, fields.getSerial());// 物理机的id
        capabilities.setCapability(AndroidConfig.autoLaunch, false);// Appium是否需要自动安装和启动应用
        capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, fields.getSystemPort());
        capabilities.setCapability(AndroidMobileCapabilityType.AVD_READY_TIMEOUT, 300000);
        capabilities.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, false);
        capabilities.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, false);

        //初始化
        URL url = new URL("http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub");

        driver = new AndroidDriver<AndroidElement>(url, capabilities);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        if (driver.isAppInstalled(AndroidConfig.appPackage)) {
            //如果安装了，先卸载
            driver.removeApp(AndroidConfig.appPackage);
        }

    /*     String installCmd = "adb install -r D:\\apkPackage\\wljs01\\apk\\vc-57-vn-1.8.0-11-26-17-54.apk";

           String e = readCmd(installCmd);

            if(e.contains(AdbException.ERROR12.getCode())){
                System.out.println("ADB命令执行安装APK包时，报错： " + AdbException.ERROR12.getMsg());
            }else{
                System.out.println(e);
            }*/
    }
}
