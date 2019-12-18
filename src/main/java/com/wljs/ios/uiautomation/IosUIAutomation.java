package com.wljs.ios.uiautomation;

import com.wljs.ios.uiautomation.handle.*;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.server.StartAppiumServer;
import com.wljs.ios.common.InitIosDriver;
import com.wljs.util.ScreenshotUtil;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSTouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;


/**
 * 执行UI自动化测试
 */
public class IosUIAutomation {
    private Logger logger = LoggerFactory.getLogger(IosUIAutomation.class);

    //截图工具类
    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();
    //初始化driver工具类
    private InitIosDriver iosDriverUtil = new InitIosDriver();
    //滑动页面工具类
    private IosSlidePageHandle iosSlidePageHandle = new IosSlidePageHandle();
    //登陆工具类
    private IosLoginHandle iosLoginHandle = new IosLoginHandle();
    //商品工具类
    private IosProductHandle iosProductHandle = new IosProductHandle();

    private LocationElement locationElement = new LocationElement();


    /**
     * 執行UI自动化测试
     *
     * @param fields   设备对象信息
     * @param phoneNum 手机尾号
     * @throws Exception
     */
    public ResponseData uiAutomation(StfDevicesFields fields, int phoneNum) {
        ResponseData responseData = new ResponseData();
        IOSDriver driver = null;
        try {

            DesiredCapabilities capabilities = iosDriverUtil.setCapabilities(fields);

            //启动未来集市APP
            driver = iosDriverUtil.initDriver(fields, capabilities);

            //向左滑动引导页
            iosSlidePageHandle.slideGuidePage(driver, fields);

            //点击【立即体验】按钮
            responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeStaticText("立即体验"));
            if (responseData.isStatus()) {
                responseData.getWebElement().click();
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【立即体验】按钮");
            }

            Dimension dimension = driver.manage().window().getSize();
            int width = dimension.width;
            int height = dimension.height;

            //向上移动（不然无法识别底部栏位【我的】按钮）
            new IOSTouchAction(driver).press(PointOption.point(width / 2, (new Double(height * 0.9)).intValue())).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                    .moveTo(PointOption.point(width / 2, (new Double(height * 0.3)).intValue())).release().perform();

            //点击【我的】按钮
            responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeButton("我的"));
            if (responseData.isStatus()) {

                responseData.getWebElement().click();
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【我的】按钮");
            }


            //切换测试环境
            responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeButton("开发环境"));
            if (responseData.isStatus()) {
                responseData.getWebElement().click();
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击【开发环境】按钮，切换至测试环境");
            }

            //模拟登录
            iosLoginHandle.login(driver, fields, phoneNum);

            //新账号需要填写邀请码
            iosLoginHandle.inviteCode(driver, fields);

            //向上滑动页面
            responseData = iosSlidePageHandle.slidePageUp(driver, fields);

            if (responseData.isStatus() && null == responseData.getExMsg()) {

                //获取"推荐好物"第一个商品名称和价格

                responseData = iosProductHandle.productList(driver, fields);

                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 测试用例执行完毕，关闭未来集市APP");
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.error("执行UI自动化测试失败: ", e);

            //截图
            String screenImg = screenshotUtil.screenshot(null, driver, fields.getSerial());

            responseData = new ResponseData(false, e, "执行UI自动化测试失败: " + e.getMessage(), screenImg);

        } finally {
            responseData.setFields(fields);

            if (null != driver) {
                driver.closeApp();
                driver.quit();
            }

            return responseData;
        }
    }


    public static void main(String[] arg) {
        StfDevicesFields fields = new StfDevicesFields();
        fields.setSerial("9251e39b63331e52538dc2cc122e72da3d652b41");
        fields.setVersion("13.2.3");
        fields.setModel(" iPhone 7 Plus");
        fields.setManufacturer("Apple");
        fields.setAppiumServerPort(4723);
        fields.setSystemPort(8200);

        StartAppiumServer server = new StartAppiumServer();
        server.start(fields);

        IosUIAutomation uiTest = new IosUIAutomation();
        uiTest.uiAutomation(fields, 1);

    }
}
