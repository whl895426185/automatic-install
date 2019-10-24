package com.wljs.server;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.server.handle.PhoneInstallStepHandle;
import com.wljs.util.constant.ConfigConstant;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 安装APK测试包
 */
public class InstallApkServer {
    private Logger logger = LoggerFactory.getLogger(InstallApkServer.class);

    //初始化安裝成功标识
    private boolean resultSuccess = true;

    /**
     * 外部调用接口，执行部署
     *
     * @param queue
     * @param apkPath
     * @return
     * @throws Exception
     */
    public StfDevicesFields init(ArrayBlockingQueue queue, String apkPath) throws Exception {
        StfDevicesFields fields = (StfDevicesFields) queue.take();

        //启动appium服务
        AppiumServer appiumServer = new AppiumServer();
        appiumServer.start(fields.getAppiumServerPort());

        //执行安装
        StfDevicesFields result = installApp(fields, apkPath);
        result.setResultSuccess(resultSuccess);
        return result;
    }

    /**
     * 安装APP,如有安装，先卸载重装
     *
     * @param fields
     * @return
     * @throws Exception
     */
    public StfDevicesFields installApp(StfDevicesFields fields, String apkPath) throws Exception {
        String device = fields.getSerial();

        //初始化参数信息
        AndroidDriver<AndroidElement> driver = initDriver(fields, apkPath);

        if (null == driver) {
            logger.error("安装部署失败，异常信息为：AndroidDriver is null");
            resultSuccess = false;
            return fields;
        }

        boolean isSuccess = true;
        int i = 0;
        do {
            //检查APP是否安装
            if (driver.isAppInstalled(ConfigConstant.appPackage)) {
                //如果安装了，先卸载
                driver.removeApp(ConfigConstant.appPackage);
            }

            logger.info("-----------------安装App Start-----------------");
            logger.info("-----------------apk包所在的本地路径为： " + apkPath + "-----------------");

            //adb命令执行安装apk（不要用appium自带的安装函数，这样无法执行后续的安装步骤）
            String installCmd = "adb -s " + device + " install " + apkPath;
            logger.info("-----------------执行adb命令安装App，安装命令为： " + installCmd + "-----------------");
            Runtime.getRuntime().exec(installCmd);

            //不同的机型调用不同的安装步骤
            PhoneInstallStepHandle installStepHandle = new PhoneInstallStepHandle();
            isSuccess = installStepHandle.installStep(fields, driver);
            i++;
        } while (!isSuccess && i < 3);

        //检测包是否安装成功
        installOk(isSuccess, driver, ConfigConstant.appPackage);

        //截图查看是否安装成功
        //screenshot(driver, device, "install");

        logger.info("-----------------安装App End-----------------");
        driver.quit();

        return fields;
    }

    /**
     * 初始化AndroidDriver
     *
     * @param fields
     * @return
     */
    private AndroidDriver<AndroidElement> initDriver(StfDevicesFields fields, String apkPath) {
        AndroidDriver<AndroidElement> driver = null;

        //获取设备名称
        logger.info("-----------------检测到移动设备信息为：device = " + fields.getSerial() + ", deviceName = " + fields.getDeviceName()
                + ", platformVersion = " + fields.getVersion() + ", appiumServerPort = " + fields.getAppiumServerPort() + ", systemPort = " + fields.getSystemPort() + "-----------------");

        //初始化负责启动服务端时的参数设置
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, fields.getDeviceName()); // 设备名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, ConfigConstant.platformName);// 平台名称
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, fields.getVersion());// 系统版本号
        capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, ConfigConstant.appPackage);// 包名
        capabilities.setCapability(MobileCapabilityType.APP, apkPath);//.ipa or .apk文件所在的本地绝对路径或者远程路径
        capabilities.setCapability(MobileCapabilityType.UDID, fields.getSerial());// 物理机的id
        capabilities.setCapability(ConfigConstant.autoLaunch, false);// Appium是否需要自动安装和启动应用
        capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, fields.getSystemPort());
        capabilities.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, false);
        capabilities.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, false);

        boolean result = true;
        try {
            URL url = new URL("http://127.0.0.1:" + fields.getAppiumServerPort() + "/wd/hub");

            logger.info("-----------------调用地址为：" + url + "-----------------");
            driver = new AndroidDriver<AndroidElement>(url, capabilities);
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        } catch (Exception e) {
            result = false;
            logger.error("安装部署过程中，发生异常，异常信息为：" + e);
        } finally {
            if (!result) {
                //利用Selenium调用浏览器，动态模拟浏览器事件，释放设备资源
                SeleniumServer seleniumServer = new SeleniumServer();
                seleniumServer.releaseResources(fields);
            }
            return driver;
        }
    }


    /**
     * 截图
     *
     * @param driver
     * @throws IOException
     */
    public void screenshot(AndroidDriver driver, String uuid, String remark) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        String dateStr = format.format(new Date());
        //生成图片的目录
        String dir_name = ConfigConstant.screenshotUrl + dateStr;
        //由于可能会存在图片的目录被删除的可能,所以我们先判断目录是否存在, 如果不在的话:
        if (!(new File(dir_name).isDirectory())) {
            //不存在的话就进行创建目录.
            new File(dir_name).mkdir();
        }
        //调用方法捕捉画面;
        File screen = driver.getScreenshotAs(OutputType.FILE);

        //复制文件到本地目录, 图片的最后存放地址为::
        FileUtils.copyFile(screen, new File(dir_name + "/" + uuid + "_" + remark + "_" + dateStr + ".jpg"));
    }


    /**
     * 检查是否安装成功
     *
     * @param driver
     * @param appPackage
     */
    private void installOk(boolean isSuccess, AndroidDriver driver, String appPackage) throws InterruptedException {
        if(!isSuccess){
            logger.info("-----------------安装App 失败-----------------");
            return;
        }
        int i = 0;
        logger.info("-----------------准备检查App是否安装成功-----------------");
        do {
            Thread.sleep(20000);

            isSuccess = driver.isAppInstalled(appPackage);

            if (!isSuccess && i == 4) {
                resultSuccess = false;
            }
            logger.info("-----------------第" + (i + 1) + "次检查App安装是否成功，结果为：" + (isSuccess ? "成功" : (i == 4 ? "失败" : "安装有点缓慢，请等待！！！！！")) + "-----------------");

            i++;
        } while (!isSuccess && i < 5);
    }


    /**
     * 执行adb命令并返回结果
     *
     * @param command
     * @return
     * @throws Exception
     */
    public String readCmd(String command) throws Exception {
        logger.info("-----------------cmd执行命令：" + command + "-----------------");

        Process process = Runtime.getRuntime().exec(command);
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

}
