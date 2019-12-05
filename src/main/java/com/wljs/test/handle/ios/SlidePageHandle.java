package com.wljs.test.handle.ios;

import com.wljs.pojo.MoveCoordinates;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.test.handle.CoordinatesHandle;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Dimension;

import java.time.Duration;

/**
 * 执行UI自动化测试，滑动页面
 */
public class SlidePageHandle {
    // private Logger logger = LoggerFactory.getLogger(SlidePageHandle.class);
    private ResponseData responseData = new ResponseData();
    private CoordinatesHandle coordinatesHandle = new CoordinatesHandle();

    /**
     * 向左滑动引导页
     * @param driver
     * @param fields
     * @return
     */
    public ResponseData slideGuidePage(IOSDriver driver, StfDevicesFields fields) {

        //获取屏幕的大小
        Dimension dimension = driver.manage().window().getSize();

        MoveCoordinates moveCoordinates = coordinatesHandle.getMoveCoordinates(dimension, 1);

        try {

            Thread.sleep(8000);
            responseData = isAppear(driver, fields, LabelConstant.slidePageOne, 2);
            if (responseData.isStatus()) {
                touchAction(driver, moveCoordinates);

            }
            responseData = isAppear(driver, fields, LabelConstant.slidePageTwo, 2);
            if (responseData.isStatus()) {

                touchAction(driver, moveCoordinates);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ResponseData();
        }
    }

    /**
     * 滑动页面
     * @param driver
     * @param moveCoordinates
     */
    private void touchAction(IOSDriver driver, MoveCoordinates moveCoordinates) {

        new TouchAction(driver).press(PointOption.point(moveCoordinates.getOrginWith(), moveCoordinates.getOrginHeight())).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                .moveTo(PointOption.point(moveCoordinates.getMoveWidth(), moveCoordinates.getMoveHeight())).release().perform();
    }


    /**
     * 向上滑动页面
     * @param driver
     * @param fields
     * @return
     */
    public ResponseData slidePageUp(IOSDriver driver, StfDevicesFields fields) {

        //获取屏幕的大小
        Dimension dimension = driver.manage().window().getSize();

        MoveCoordinates moveCoordinates = coordinatesHandle.getMoveCoordinates(dimension, 2);

        responseData = isAppear(driver, fields, LabelConstant.myOrderBtnName, 1);
        if (responseData.isStatus()) {

            touchAction(driver, moveCoordinates);
        }

        return responseData;
    }

    /**
     * 显示等待元素出现
     * @param driver
     * @param fields
     * @param text
     * @param type
     * @return
     */
    private ResponseData isAppear(IOSDriver driver, StfDevicesFields fields, String text, int type) {
        WaitElementHandle elementHandle = new WaitElementHandle();
        return elementHandle.isAppear(driver, fields, text, type);
    }
}
