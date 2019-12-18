package com.wljs.andorid.install;

import com.wljs.server.StartAppiumServer;
import com.wljs.andorid.common.AdbException;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.andorid.install.phone.handle.PhoneInstallType;
import com.wljs.andorid.common.InitAndroidDriver;
import com.wljs.util.CommandUtil;
import com.wljs.config.AppConfig;
import com.wljs.util.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 部署安装包
 */
public class AndroidInstall {
    private Logger logger = LoggerFactory.getLogger(AndroidInstall.class);

    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();
    private CommandUtil commandUtil = new CommandUtil();
    private AdbException adb = new AdbException();

    /**
     * 执行命令安装卸载
     *
     * @param fields
     * @param path
     * @return
     */
    public ResponseData installApp(StfDevicesFields fields, String path) {

        ResponseData responseData = new ResponseData();
        boolean isSuccess = false;
        String process = "";
        try {
            //初始化参数信息
            InitAndroidDriver androidDriverUtil = new InitAndroidDriver();
            DesiredCapabilities capabilities = androidDriverUtil.setCapabilities(fields, path);

            // Appium是否需要自动安装和启动应用
            capabilities.setCapability(AppConfig.autoLaunch, false);


            //启动appium
            StartAppiumServer server = new StartAppiumServer();
            server.start(fields);

            //初始化driver
            AndroidDriver<AndroidElement> driver = androidDriverUtil.initDriver(fields, capabilities);


            //先执行卸载包
            if (driver.isAppInstalled(AppConfig.appPackage)) {

                boolean deleteFlag = driver.removeApp(AppConfig.appPackage);

                if(deleteFlag){
                    process = "Success";
                    logger.info("::::::::::::::::: <<<" + fields.getSerial() + ">>> 执行命令卸载包结果：成功");
                }

            } else {
                process = "Success";
            }

            if (process.contains("Success")) {

                //执行安装包
                String installCmd = "adb -s " + fields.getSerial() + " install " + path;
                Process installProcess = Runtime.getRuntime().exec(installCmd);

                Thread.sleep(5000);

                //不同的机型调用不同的安装步骤
                PhoneInstallType installStepHandle = new PhoneInstallType();
                responseData = installStepHandle.installStep(fields, driver);

                if (!responseData.isStatus()) {
                    responseData.setImagePath(screenshotUtil.screenshot(driver, null, fields.getSerial()));
                } else {

                    //检查是否安装成功
                    int i = 0;
                    do {
                        Thread.sleep(20000);
                        if (driver.isAppInstalled(AppConfig.appPackage)) {
                            isSuccess = true;
                            logger.info("::::::::::::::::: <<<" + fields.getSerial() + ">>> 执行命令安装包结果：成功");
                        }
                        i++;

                    } while (!isSuccess && i < 5);

                }

                process = commandUtil.getProcessStr(installProcess, fields);

                if(process.contains("Success")){
                    isSuccess = true;
                }

                driver.quit();
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行自动部署安装失败：" + e);

            responseData = new ResponseData(false, e, "执行自动部署安装失败：" + e);


        } finally {

            if (!isSuccess) {
                String adbExeMsg = adb.adbException(process);

                if (null != adbExeMsg) {
                    //ADB安裝包失敗，沒有直接抛出異常，所以手動維護異常
                    responseData = new ResponseData(false, null, "ADB命令执行安装APK包时,报错异常：" + adbExeMsg, null, process);

                }
            }

            responseData.setFields(fields);
            return responseData;
        }

    }


    public static void main(String[] arg) throws Exception {
        StfDevicesFields fields = new StfDevicesFields();
//        fields.setSerial("1d4bc416");
//        fields.setVersion("7.1.2");
//        fields.setModel(" X9");
//        fields.setManufacturer("VIVO");

        fields.setSerial("612QKBQJ226WJ");
        fields.setVersion("6.0");
        fields.setModel(" M5s");
        fields.setManufacturer("MEIZU");

        fields.setAppiumServerPort(4723);
        fields.setSystemPort(8200);

        String apkPath = "/Volumes/Tools/test/vc-61-vn-1.8.1-12-06-11-40.apk";

        AndroidInstall install = new AndroidInstall();
        install.installApp(fields, apkPath);
    }

}
