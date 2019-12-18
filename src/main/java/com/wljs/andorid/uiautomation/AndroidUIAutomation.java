package com.wljs.andorid.uiautomation;

import com.wljs.andorid.common.LocationElement;
import com.wljs.andorid.uiautomation.handle.*;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.andorid.common.InitAndroidDriver;
import com.wljs.server.StartAppiumServer;
import com.wljs.util.CommandUtil;
import com.wljs.config.AppConfig;
import com.wljs.util.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行UI自动化测试
 * （1）查看是否可以正常登陆（初始化安装测试包之后，第一次登陆会提示验证码不正确，需要登陆两次）
 * （2）查看【我的】页面，第一个商品的名称和价格是否显示
 */
public class AndroidUIAutomation {
    private Logger logger = LoggerFactory.getLogger(AndroidUIAutomation.class);

    //截图工具类
    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();
    //初始化driver工具类
    private InitAndroidDriver initAndroidDriver = new InitAndroidDriver();
    //弹框处理工具
    private AndroidElasticFrameHandle androidElasticFrameHandle = new AndroidElasticFrameHandle();
    //滑动页面工具类
    private AndroidSlidePageHandle androidSlidePageHandle = new AndroidSlidePageHandle();
    //登陆工具类
    private AndroidLoginHandle androidLoginHandle = new AndroidLoginHandle();
    //商品工具类
    private AndroidProductHandle androidProductHandle = new AndroidProductHandle();
    //定位元素
    private LocationElement locationElement = new LocationElement();

    private AndroidDriver driver;

    /**
     * 執行UI自动化测试
     *
     * @param fields   设备对象信息
     * @param path     apk文件绝对路径
     * @param phoneNum 手机尾号
     * @throws Exception
     */
    public ResponseData uiAutomation(StfDevicesFields fields, String path, int phoneNum) {
        ResponseData responseData = new ResponseData();
        try {

            DesiredCapabilities capabilities = initAndroidDriver.setCapabilities(fields, path);

            for (int i = 1; i <= 2; i++) {

                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 第" + i + "次启动未来集市APP");

                //启动APP
                driver = initAndroidDriver.initDriver(fields, capabilities);


                //跳转到主页(滑动引导页)
                if (1 == i) {
                    //点击弹框允许操作
                    androidElasticFrameHandle.elasticFrameHandle(driver, fields.getDeviceName());

                    //向左滑动引导页
                    androidSlidePageHandle.slideGuidePage(driver, fields);

                    //点击【立即体验】按钮
                    responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getResourceId("startTextView"));
                    if (responseData.isStatus()) {
                        responseData.getWebElement().click();
                        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【立即体验】按钮");
                    }

                }

                //点击【我的】按钮
                responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getResourceId("ll_mine"));
                if (responseData.isStatus()) {
                    responseData.getWebElement().click();
                    logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【我的】按钮");
                }

                //点击其他方式
                responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getResourceId("tv_phone_login"));
                if (responseData.isStatus()) {
                    responseData.getWebElement().click();
                    logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【其他方式登陆】按钮");
                }

                //点击【测试】按钮
                responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getResourceId("rbtn_test"));
                if (responseData.isStatus()) {
                    responseData.getWebElement().click();
                    logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【测试】按钮");
                }

                //模拟登录
                responseData = androidLoginHandle.login(driver, fields, phoneNum, i);

                //停留一分钟
                Thread.sleep(10000);

                if (1 == i) {//切换环境需杀掉进程
                    driver.closeApp();
                    driver.quit();

                    responseData = killProcess(fields);
                }

                if (2 == i) {
                    if(responseData.isStatus()){
                        //新账号需要填写邀请码
                        androidLoginHandle.inviteCode(driver, fields);

                        //向上滑动页面
                        responseData = androidSlidePageHandle.slidePageUp(driver, fields);

                        if (responseData.isStatus() && null == responseData.getExMsg()) {

                            //获取"推荐好物"第一个商品名称和价格
                            responseData = androidProductHandle.productList(driver, fields);

                            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 测试用例执行完毕，关闭未来集市APP");
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("执行UI自动化测试失败: ", e);

            String screenImg = screenshotUtil.screenshot(driver, null, fields.getSerial());

            responseData.setImagePath(screenImg);

        } finally {
            if (null != driver) {
                driver.closeApp();
                driver.quit();
            }

            responseData.setFields(fields);
            return responseData;
        }
    }


    /**
     * 杀进程
     *
     * @param fields
     */
    private ResponseData killProcess(StfDevicesFields fields) {

        //命令执行工具类
        CommandUtil commandUtil = new CommandUtil();

        String killCommand = "adb -s " + fields.getSerial() + " shell am force-stop " + AppConfig.appPackage;

        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 切换环境，执行ADB命令杀掉APP进程： " + killCommand);

        String killProcess = commandUtil.getProcess(killCommand, fields);

        if (null == killProcess || ("").equals(killProcess)) {
            //查看进程是否还存在
            String command = "adb -s " + fields.getSerial() + " shell ps|grep " + AppConfig.appPackage;
            String process = commandUtil.getProcess(command, fields);

            if (null == process || ("").equals(process)) {
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: APP进程已杀");
            }
        }

        return new ResponseData();


    }


    public static void main(String[] arg) throws Exception {
        StfDevicesFields fields = new StfDevicesFields();
        fields.setSerial("8KE5T19711012159");
        fields.setVersion("9.0");
        fields.setModel(" P30");
        fields.setManufacturer("HUAWEI");
        fields.setAppiumServerPort(4723);
        fields.setSystemPort(8200);

        String apkPath = "/Volumes/Tools/test/vc-61-vn-1.8.1-12-06-11-40.apk";

        StartAppiumServer server = new StartAppiumServer();
        server.start(fields);

        AndroidUIAutomation uiAutomation = new AndroidUIAutomation();
        uiAutomation.uiAutomation(fields, apkPath, 1);
    }
}
