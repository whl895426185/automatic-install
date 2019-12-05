package com.wljs.test;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.test.handle.ios.LoginHandle;
import com.wljs.test.handle.ios.ProductHandle;
import com.wljs.test.handle.ios.SlidePageHandle;
import com.wljs.test.handle.ios.WaitElementHandle;
import com.wljs.util.CommandUtil;
import com.wljs.util.IosDriverUtil;
import com.wljs.util.ScreenshotUtil;
import com.wljs.util.TxtUtil;
import com.wljs.util.constant.CommandConstant;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 执行UI自动化测试
 * （1）查看是否可以正常登陆（初始化安装测试包之后，第一次登陆会提示验证码不正确，需要登陆两次）
 * （2）查看【我的】页面，第一个商品的名称和价格是否显示
 */
public class IosUIAutomation extends WaitElementHandle {
    private Logger logger = LoggerFactory.getLogger(IosUIAutomation.class);
    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();
    private IosDriverUtil iosDriverUtil = new IosDriverUtil();

    private IOSDriver driver;

    /**
     * 執行UI自动化测试
     *
     * @param fields   设备对象信息
     * @param path     ipa文件绝对路径
     * @param phoneNum 手机尾号
     * @throws Exception
     */
    public ResponseData uiAutomation(StfDevicesFields fields, String path, int phoneNum) {
        ResponseData responseData = new ResponseData();
        try {
            DesiredCapabilities capabilities = iosDriverUtil.setCapabilities(fields);

            //第1次启动未来集市App
            responseData = fristStartApp(fields, capabilities, phoneNum);


            if (responseData.isStatus()) {

                //第2次启动未来集市App
                responseData = secondStartApp(fields, capabilities, phoneNum);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("执行UI自动化测试失败: ", e);
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("执行UI自动化测试失败: " + e.getMessage());
            responseData.setImagePath(screenshotUtil.screenshotForIos(driver, fields.getSerial()));

        } finally {
            responseData.setFields(fields);
            return responseData;
        }
    }

    //第一次启动APP
    private ResponseData fristStartApp(StfDevicesFields fields, DesiredCapabilities capabilities, int phoneNum) {
           ResponseData responseData = new ResponseData();
        try {
            //向左滑动引导页
            SlidePageHandle pageHandle = new SlidePageHandle();
            pageHandle.slideGuidePage(driver, fields);

            //点击【立即体验】按钮
            responseData = isAppear(driver, fields, LabelConstant.experienceBtnName, 1);
            if (responseData.isStatus()) {
                tap(driver, fields, LabelConstant.experienceBtnName, 1);
            }

            //检测是否发现新版本
            responseData = isAppear(driver, fields, LabelConstant.findNewVersionName, 1);
            if (responseData.isStatus()) {
                driver.findElement(By.xpath("//*//*[@text='" + LabelConstant.ignoreBtnName + "']")).click();
            }

            //点击【我的】按钮
            responseData = isAppear(driver, fields, LabelConstant.mineBtnName, 1);
            if (responseData.isStatus()) {
                driver.findElement(By.xpath(LabelConstant.mineBtn)).click();
            }

            //点击其他方式（兼容新版登录）
            responseData = isAppear(driver, fields, LabelConstant.otherLoginBtnName, 1);
            if (responseData.isStatus()) {
                driver.findElement(By.xpath("//*//*[@text='" + LabelConstant.otherLoginBtnName + "']")).click();
            }


            //点击【测试】按钮
            responseData = isAppear(driver, fields, LabelConstant.textBtnName, 1);
            if (responseData.isStatus()) {
                driver.findElement(By.xpath(LabelConstant.testBtn)).click();
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【测试】按钮");
            }

            //模拟登录
//            LoginHandle loginHandle = new LoginHandle();
//            loginHandle.login(driver, fields, phoneTailNumber);

            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行UI自动化测试失败: " + e);
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("执行UI自动化测试失败: " + e.getMessage());
        } finally {
            try {
                if (!responseData.isStatus()) {
                    responseData.setImagePath(screenshotUtil.screenshotForIos(driver, fields.getSerial()));
                }
                driver.closeApp();
                driver.quit();
                //殺進程
                CommandUtil commandUtil = new CommandUtil();
                String processMsg = commandUtil.getProcess(CommandConstant.killProcessCommand);
                logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 切换环境，手动杀掉APP进程： " + processMsg);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 手动杀掉APP进程失败： " + e);
                responseData.setStatus(false);
                responseData.setException(e);
                responseData.setExMsg("手动杀掉APP进程： 失败");
            }
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 关闭未来集市APP");
            return responseData;
        }


    }

    /**
     * 第二次启动
     */
    private ResponseData secondStartApp(StfDevicesFields fields, DesiredCapabilities capabilities, int phoneTailNumber) {
        ResponseData responseData = new ResponseData();
        try {
            //点击【我的】按钮
            responseData = isAppear(driver, fields, LabelConstant.mineBtnName, 1);
            if (responseData.isStatus()) {
                driver.findElement(By.xpath(LabelConstant.mineBtn)).click();
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【我的】按钮");
            }
            //点击其他方式（兼容新版登录）
            responseData = isAppear(driver, fields, LabelConstant.otherLoginBtnName, 1);
            if (responseData.isStatus()) {
                driver.findElement(By.xpath("//*//*[@text='" + LabelConstant.otherLoginBtnName + "']")).click();
            }

            //点击【测试】按钮
            responseData = isAppear(driver, fields, LabelConstant.textBtnName, 1);
            if (responseData.isStatus()) {
                driver.findElement(By.xpath(LabelConstant.testBtn)).click();
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【测试】按钮");
            }

            //模拟登录
            LoginHandle loginHandle = new LoginHandle();
            loginHandle.login(driver, fields, phoneTailNumber);

            //新账号需要填写邀请码
            loginHandle.inviteCode(driver, fields);

            //向上滑动页面
            SlidePageHandle pageHandle = new SlidePageHandle();
            responseData = pageHandle.slidePageUp(driver, fields);

            if (responseData.isStatus() && null == responseData.getExMsg()) {

                //获取"推荐好物"第一个商品名称和价格
                ProductHandle productHandle = new ProductHandle();
                responseData = productHandle.productList(driver, fields);

                if (responseData.isStatus()) {
                    //点击图片进入商品详情
                    responseData = productHandle.productDetail(driver, fields);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("执行UI自动化测试失败: " + e.getMessage());
        } finally {
            try {
                if (!responseData.isStatus()) {
                    responseData.setImagePath(screenshotUtil.screenshotForIos(driver, fields.getSerial()));
                }
                driver.closeApp();

            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 测试用例执行完毕，关闭未来集市APP");
            driver.quit();
            return responseData;
        }
    }

    public static void main(String[] arg) throws Exception {
        /*StfDevicesFields fields = new StfDevicesFields();
        fields.setSerial("8KE5T19711012159");
        fields.setVersion("9.0");
        fields.setModel(" P30");
        fields.setManufacturer("HUAWEI");
        fields.setAppiumServerPort(4723);
        fields.setSystemPort(8200);

        AppiumServer server = new AppiumServer();
        server.start(4723);

        UIAutomationTest uiTest = new UIAutomationTest();
        String apkPath = "D:\\apkPackage\\wljs01\\apk\\vc-56-vn-1.7.0-11-07-15-56.apk";
        uiTest.executeTest(fields, apkPath, 1);*/
        TxtUtil txtUtil = new TxtUtil();
        String txt = txtUtil.readTxtFile("D:\\apkPackage", "15bc4db9b0484fc088c37d0dcaaa557b.txt");
        System.out.println(txt.split("::")[1]);
        System.out.println(txt.split("::").length - 2);

    }
}
