package com.wljs.test.handle;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 执行UI自动化测试，滑动页面
 */
public class SlidePageHandle {
    private Logger logger = LoggerFactory.getLogger(SlidePageHandle.class);

    //向左滑动引导页
    public void slideGuidePage(AndroidDriver driver, StfDevicesFields fields, int width, int height) {
        int orginWith = (new Double(width * 0.9)).intValue();
        int orginHeight = height / 2;
        int moveWidth = (new Double(width * 0.15)).intValue();
        int moveHeight = height / 2;

        if (isAppear(driver, fields, LabelConstant.slidePageOne, 2)) {
            new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                    .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();
        }
        if (isAppear(driver, fields, LabelConstant.slidePageTwo, 2)) {
            new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                    .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();
        }
    }

    //向上滑动页面
    public ResponseData slidePageUp(AndroidDriver driver, StfDevicesFields fields, int width, int height) {
        ResponseData responseData = new ResponseData();
        //向上滑动
        if (isAppear(driver, fields, LabelConstant.mineBtnName, 1)) {
            logger.info(":::::::::::::::::【" + fields.getDeviceName() + "】模拟向上滑动页面，查看商品信息");

            int orginWith = width / 2;
            int orginHeight = (new Double(height * 0.9)).intValue();
            int moveWidth = width / 2;
            int moveHeight = new Double(height * 0.1).intValue();

            new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                    .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();

        } else {
            responseData.setExMsg(":::::::::::::::::【" + fields.getDeviceName() + "】没有获取到元素： //*//*[@text='" + LabelConstant.mineBtnName + "']");
        }

        return responseData;
    }

    private boolean isAppear(AndroidDriver driver, StfDevicesFields fields, String text, int type) {
        WaitElementHandle elementHandle = new WaitElementHandle();
        return elementHandle.isAppear(driver, fields, text, type);
    }
}
