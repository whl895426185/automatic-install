package com.wljs.install;

import com.wljs.appium.StartAppium;
import com.wljs.driver.InitAndroidDriver;
import com.wljs.install.step.InstallType;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.CommandUtil;
import com.wljs.util.ExceptionUtil;
import com.wljs.util.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * 线程并发执行
 */
public class ThreadConcurrent implements Callable {
    private Logger logger = LoggerFactory.getLogger(ThreadConcurrent.class);

    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();
    private CommandUtil commandUtil = new CommandUtil();
    private ExceptionUtil exceptionUtil = new ExceptionUtil();

    private String appPackage = "com.sibu.futurebazaar";

    private ArrayBlockingQueue queue;
    private String androidFile;//apk包绝对路径
    private String iosFile;//ipa包绝对路径


    public ThreadConcurrent(ArrayBlockingQueue queue, String androidFile, String iosFile) {
        this.queue = queue;
        this.androidFile = androidFile;
        this.iosFile = iosFile;
    }

    @Override
    public ResponseData call() throws Exception {
        ResponseData responseData = new ResponseData();
        try {

            logger.info("::::::::::::::::: 线程" + Thread.currentThread().getName() + "开始执行");

            //准备安装工作
            StfDevicesFields fields = (StfDevicesFields) queue.take();

            //执行安装
            if (("Android").equals(fields.getPlatform())) {
                responseData = androidInstall(fields, androidFile);

            } else if (("iOS").equals(fields.getPlatform())) {
                responseData = iOSInstall(fields, iosFile);
            }

            responseData.setFields(fields);

            logger.info(":::::::::::::::::<<<" + responseData.getFields().getDeviceName() + ">>>::::::::::::::::: 执行自动部署安装："
                    + (responseData.isStatus() ? "成功" : "失败"));

            logger.info(":::::::::::::::::<<<" + responseData.getFields().getDeviceName() + ">>>::::::::::::::::: 线程" + Thread.currentThread().getName() + "执行完毕");
        } catch (Exception e) {
            e.printStackTrace();

            String exceptionMsg = exceptionUtil.exceptionMsg(e);
            responseData = new ResponseData(false, "执行自动部署安装失败", exceptionMsg, e);

        } finally {

            return responseData;
        }
    }


    /**
     * #############################################################################
     * ########################### Android apk #################### Start ##########
     * #############################################################################
     */
    public ResponseData androidInstall(StfDevicesFields fields, String path) {
        ResponseData responseData = new ResponseData();
        boolean isSuccess = false;
        String process = "";
        try {
            //启动appium
            StartAppium server = new StartAppium();
            server.startAppiumServer(fields);

            //初始化driver
            InitAndroidDriver init = new InitAndroidDriver();
            Map<String, Object> resultMap = init.initDriver(fields, path);

            AndroidDriver<AndroidElement> driver = (AndroidDriver<AndroidElement>) resultMap.get("AndroidDriver");
            responseData = (ResponseData) resultMap.get("responseData");


            if (null != driver) {
                //先执行卸载包
                if (driver.isAppInstalled(appPackage)) {
                    boolean deleteFlag = driver.removeApp(appPackage);
                    if (deleteFlag) {
                        process = "Success";
                        logger.info("::::::::::::::::: <<<" + fields.getDeviceName() + ">>> 执行命令卸载包结果：成功");
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
                    InstallType installStepHandle = new InstallType();
                    responseData = installStepHandle.installStep(fields, driver);

                    if (!responseData.isStatus()) {
                        responseData.setImagePath(screenshotUtil.screenshot(driver, null, fields.getSerial()));
                    } else {
                        //检查是否安装成功
                        int i = 0;
                        do {
                            Thread.sleep(20000);
                            if (driver.isAppInstalled(appPackage)) {
                                isSuccess = true;
                                logger.info("::::::::::::::::: <<<" + fields.getDeviceName() + ">>> 执行命令安装包结果：成功");
                            }
                            i++;
                        } while (!isSuccess && i < 5);
                    }

                    process = commandUtil.getProcessStr(installProcess, fields);

                    if (process.contains("Success")) {
                        isSuccess = true;
                    }
                    driver.quit();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            String exceptionMsg = exceptionUtil.exceptionMsg(e);
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行自动部署安装失败：" + exceptionMsg);

            responseData = new ResponseData(false, "执行自动部署安装失败", exceptionMsg, e);

        } finally {

            if (!isSuccess) {
                String adbExeMsg = exceptionUtil.adbExceptionMsg(process);

                if (null != adbExeMsg) {
                    //ADB安裝包失敗，沒有直接抛出異常，所以手動維護異常
                    responseData = new ResponseData(false, "ADB命令执行安装APK包时,报错异常：" + adbExeMsg, process, null);

                }
            }
            responseData.setFields(fields);
            return responseData;
        }
    }


    /**
     * #############################################################################
     * ########################### ios apk ######################## Start ##########
     * #############################################################################
     */
    public ResponseData iOSInstall(StfDevicesFields fields, String path) {

        ResponseData responseData = new ResponseData();
        String iosProcess = "";
        boolean isSuccess = false;
        try {
            //先执行卸载app
            String uninstallCmd = "ideviceinstaller -u " + fields.getSerial() + " -U " + appPackage;

            iosProcess = commandUtil.getProcess(uninstallCmd, fields);


            if (iosProcess.contains("Complete")) {
                logger.info("::::::::::::::::: <<<" + fields.getDeviceName() + ">>> 执行命令卸载包结果：成功");

                //执行安装app
                String installCmd = "ideviceinstaller -u " + fields.getSerial() + " -i " + path;
                iosProcess = commandUtil.getProcess(installCmd, fields);

                //检查app是否安装完毕
                int i = 0;
                do {
                    Thread.sleep(20000);

                    String checkCommand = "ideviceinstaller -l";
                    iosProcess = commandUtil.getProcess(checkCommand, fields);

                    if (iosProcess.contains(appPackage)) {
                        logger.info("::::::::::::::::: <<<" + fields.getDeviceName() + ">>> 执行命令安装包结果：成功");
                        isSuccess = true;
                    }
                    i++;
                } while (!isSuccess && i < 3);
            }
        } catch (Exception e) {
            e.printStackTrace();

            String exceptionMsg = exceptionUtil.exceptionMsg(e);
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行自动部署安装失败：" + exceptionMsg);

            responseData = new ResponseData(false, "执行自动部署安装失败", exceptionMsg, e);
        } finally {
            if (!isSuccess) {
                responseData = new ResponseData(false, "执行自动部署安装失败", iosProcess, null);

            }
            responseData.setFields(fields);
            return responseData;
        }
    }
}
