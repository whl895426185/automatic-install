package com.wljs.test;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.server.InstallApkServer;
import com.wljs.test.handle.*;
import com.wljs.util.constant.*;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行UI自动化测试
 * （1）查看是否可以正常登陆（初始化安装测试包之后，第一次登陆会提示验证码不正确，需要登陆两次）
 * （2）查看【我的】页面，第一个商品的名称和价格是否显示
 */
public class UIAutomationTest extends WaitElementHandle{
    private Logger logger = LoggerFactory.getLogger(UIAutomationTest.class);

    private AndroidDriver driver;

    /**
     * 執行UI自动化测试
     * @param fields 设备对象信息
     * @param appPath apk绝对路径
     * @param phoneNum 手机尾号
     * @throws Exception
     */
    public void executeTest(StfDevicesFields fields, String appPath, int phoneNum) throws Exception {
        //启动APP
        for (int i = 0; i < 2; i++) {
            logger.info("---------------第" + (i + 1) + "次启动未来集市APP---------------");

            //启动APP
            StartUpAppHandle startUpAppHandle = new StartUpAppHandle();
            driver = startUpAppHandle.startUpApp(fields, appPath);

            //获取屏幕的大小
            Dimension dimension = driver.manage().window().getSize();
            int width = dimension.width;
            int height = dimension.height;
            logger.info("手机屏幕：with = " + width + ", height = " + height);

            if (i == 0) { //第一次启动
                if(firstStartApp(fields.getDeviceName(), width, height, phoneNum)){
                    break;
                }
            }
            if (i == 1) {//第二次启动
                secondStartApp(width, height, phoneNum);
            }
        }
    }

    /**
     * 第一次启动APP
     *
     * @param deviceName
     * @param width
     * @param height
     * @throws Exception
     */
    private boolean firstStartApp(String deviceName, int width, int height, int phoneTailNumber) throws Exception {
        boolean findNewVersionFlag = false;//初始化识别新版本标识

        //点击弹框允许操作
        ElasticFrameHandle frameHandle = new ElasticFrameHandle();
        frameHandle.elasticFrameHandle(driver, deviceName);

        //向左滑动引导页
        SlidePageHandle pageHandle = new SlidePageHandle();
        pageHandle.slideGuidePage(driver, width, height);

        //点击【立即体验】按钮
        if (isAppear(driver, LabelConstant.experienceBtnName, 1)) {
            tap(driver, LabelConstant.experienceBtnName, 1);
        }
        //检测是否发现新版本
        if (isAppear(driver, LabelConstant.findNewVersionName, 1)) {
            findNewVersionFlag = true;
            logger.info("---------------发现新版本，需要更新后才能使用，不再继续执行UI自动化测试---------------");
            logger.info("---------------通过测试用例检测，安装测试包【成功】！！！---------------");

            //关闭APP
            driver.closeApp();
            driver.quit();
            logger.info("---------------测试用例执行完毕，关闭未来集市APP---------------");
        }
        if(!findNewVersionFlag){
            //点击【我的】按钮
            if (isAppear(driver, LabelConstant.mineBtnName, 1)) {
                driver.findElement(By.xpath(LabelConstant.mineBtn)).click();
            }

            //点击其他方式（兼容新版登录）
            isAppear(driver, LabelConstant.otherLoginBtnName, 1);

            //模拟登录
            LoginHandle loginHandle = new LoginHandle();
            loginHandle.login(driver, phoneTailNumber);

            logger.info("---------------停留1分钟后，准备重启APP---------------");
            Thread.sleep(10000);

            //关闭APP
            driver.closeApp();

            //第一次启动后需杀掉进程，不然手机号码验证码验证不正确
            InstallApkServer installApkServer = new InstallApkServer();
            installApkServer.readCmd(CommandConstant.killProcessCommand);

            driver.quit();
        }
        return findNewVersionFlag;
    }

    /**
     * 第二次启动
     *
     * @param width
     * @param height
     */
    private void secondStartApp(int width, int height, int phoneTailNumber) {
        //点击【我的】按钮
        if (isAppear(driver, LabelConstant.mineBtnName, 1)) {
            driver.findElement(By.xpath(LabelConstant.mineBtn)).click();
            logger.info("---------------模拟点击【我的】按钮---------------");
        }
        //点击其他方式（兼容新版登录）
        isAppear(driver, LabelConstant.otherLoginBtnName, 1);

        //模拟登录
        LoginHandle loginHandle = new LoginHandle();
        loginHandle.login(driver, phoneTailNumber);

        //新账号需要填写邀请码
        loginHandle.inviteCode(driver);

        //向上滑动页面
        SlidePageHandle pageHandle = new SlidePageHandle();
        pageHandle.slidePageUp(driver, width, height);

        //获取"推荐好物"第一个商品名称和价格
        ProductHandle productHandle = new ProductHandle();
        boolean isSuccess = productHandle.productList(driver);

        //点击图片进入商品详情
        productHandle.productDetail(driver);

        logger.info("---------------通过测试用例检测，安装测试包【" + (isSuccess ? "成功" : "失败") + "】！！！---------------");

        driver.closeApp();

        logger.info("---------------测试用例执行完毕，关闭未来集市APP---------------");

        //截图检测APP是否已安装成功
        //installApkServer.screenshot(driver, device, "testcase");

        driver.quit();
    }

    /*public static void main(String[] arg) throws Exception {
        StfDevicesFields fields = new StfDevicesFields();
        fields.setSerial("8KE5T19711012159");
        fields.setVersion("9.0");
        fields.setModel(" P30");
        fields.setManufacturer("HUAWEI");
        fields.setAppiumServerPort(4723);
        fields.setSystemPort(8200);

        AppiumServer server = new AppiumServer();
        server.start(4723);

        UIAutomationTest uiTest = new UIAutomationTest();
        String apkPath = "D:\\apkPackage\\wljs01\\apk\\vc-51-vn-1.5.5-10-23-12-12.apk";
        uiTest.executeTest(fields, apkPath, 1);

    }
*/
}
