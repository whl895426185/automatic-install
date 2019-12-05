package com.wljs.server.install;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.server.expection.AdbException;
import com.wljs.server.handle.PhoneInstallStepHandle;
import com.wljs.util.AndroidDriverUtil;
import com.wljs.util.CommandUtil;
import com.wljs.util.ScreenshotUtil;
import com.wljs.util.config.AppConfig;
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

    /**
     * 执行命令安装卸载
     *
     * @param fields
     * @param path
     * @return
     */
    public ResponseData installApp(StfDevicesFields fields, String path) {

        ResponseData responseData = new ResponseData();
        String process = "";
        try {

            //先执行卸载包
            String uninstallCmd = "adb -s " + fields.getSerial() + " uninstall " + AppConfig.appPackage;

            process = commandUtil.getProcess(uninstallCmd);

            logger.info("::::::::::::::::: <<<" + fields.getSerial() + ">>> 执行命令卸载包结果：" + process);

            if (process.contains("Success")) {

                //执行安装包
                String installCmd = "adb -s " + fields.getSerial() + " install " + path;

                process = commandUtil.getProcess(installCmd);


                logger.info("::::::::::::::::: <<<" + fields.getSerial() + ">>> 执行命令安装包结果：" + process);

                if (process.contains("Success")) {

                    //初始化参数信息
                    AndroidDriverUtil androidDriverUtil = new AndroidDriverUtil();
                    DesiredCapabilities capabilities = androidDriverUtil.setCapabilities(fields, path);

                    // Appium是否需要自动安装和启动应用
                    capabilities.setCapability(AppConfig.autoLaunch, false);

                    //初始化driver
                    AndroidDriver<AndroidElement> driver = androidDriverUtil.initDriver(fields, capabilities);

                    //不同的机型调用不同的安装步骤
                    PhoneInstallStepHandle installStepHandle = new PhoneInstallStepHandle();
                    responseData = installStepHandle.installStep(fields, driver);
                    if (!responseData.isStatus()) {
                        responseData.setImagePath(screenshotUtil.screenshotForAndroid(driver, fields.getSerial()));
                    }

                    driver.quit();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行自动部署安装失败：" + e);
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("执行自动部署安装失败：" + e);

        } finally {

            if (!process.contains("Success")) {

                AdbException adb = new AdbException();
                String adbExeMsg = adb.adbException(process);

                if (null != adbExeMsg) {
                    //ADB安裝包失敗，沒有直接抛出異常，所以手動維護異常
                    responseData.setExMsg("ADB命令执行安装APK包时,报错异常：" + adbExeMsg);
                    responseData.setAdbExceptionMsg(process);
                    responseData.setStatus(false);
                    responseData.setException(null);//设置为空，这样钉钉消息才会读取adbExceptionMsg
                }
            }

            responseData.setFields(fields);
            return responseData;
        }

    }


}
