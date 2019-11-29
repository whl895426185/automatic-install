package com.wljs.test.handle;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 执行UI自动化测试，滑动页面
 */
public class SlidePageHandle {
    private Logger logger = LoggerFactory.getLogger(SlidePageHandle.class);

    //向左滑动引导页
    public ResponseData slideGuidePage(AndroidDriver driver, StfDevicesFields fields) {
        ResponseData responseData = new ResponseData();
        //获取屏幕的大小
        Dimension dimension = driver.manage().window().getSize();
        int width = dimension.width;
        int height = dimension.height;
        try{
            int orginWith = (new Double(width * 0.9)).intValue();
            int orginHeight = height / 2;
            int moveWidth = (new Double(width * 0.15)).intValue();
            int moveHeight = height / 2;

            Thread.sleep(8000);
            responseData = isAppear(driver, fields, LabelConstant.slidePageOne, 2);
            if (responseData.isStatus()) {
                new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                        .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();
            }
            responseData = isAppear(driver, fields, LabelConstant.slidePageTwo, 2);
            if (responseData.isStatus()) {
                new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                        .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return new ResponseData();
        }
    }

    //向上滑动页面
    public ResponseData slidePageUp(AndroidDriver driver, StfDevicesFields fields) {
        ResponseData responseData = new ResponseData();
        //获取屏幕的大小
        Dimension dimension = driver.manage().window().getSize();
        int width = dimension.width;
        int height = dimension.height;

        //向上滑动
        responseData = isAppear(driver, fields, LabelConstant.myOrderBtnName, 1);
        if (responseData.isStatus()) {
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟向上滑动页面，查看商品信息");

            int orginWith = width / 2;
            int orginHeight = (new Double(height * 0.9)).intValue();
            int moveWidth = width / 2;
            int moveHeight = new Double(height * 0.1).intValue();

            new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                    .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();

        }

        return responseData;
    }

    private ResponseData isAppear(AndroidDriver driver, StfDevicesFields fields, String text, int type) {
        WaitElementHandle elementHandle = new WaitElementHandle();
        return elementHandle.isAppear(driver, fields, text, type);
    }
}
