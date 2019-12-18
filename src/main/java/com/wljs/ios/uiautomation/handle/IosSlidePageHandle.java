package com.wljs.ios.uiautomation.handle;

import com.wljs.pojo.MoveCoordinates;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.MoveCoordinatesUtil;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSTouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Dimension;

import java.time.Duration;

/**
 * 执行UI自动化测试，滑动页面
 */
public class IosSlidePageHandle {
    // private Logger logger = LoggerFactory.getLogger(SlidePageHandle.class);
    private ResponseData responseData = new ResponseData();

    private LocationElement locationElement = new LocationElement();

    private MoveCoordinatesUtil coordinatesUtil = new MoveCoordinatesUtil();

    /**
     * 向左滑动引导页
     *
     * @param driver
     * @param fields
     * @return
     */
    public ResponseData slideGuidePage(IOSDriver driver, StfDevicesFields fields) {
        //获取屏幕的大小
        Dimension dimension = driver.manage().window().getSize();

        MoveCoordinates moveCoordinates = coordinatesUtil.getMoveCoordinates(dimension, 1);

        try {
            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.pageXpath);
            if (responseData.isStatus()) {
                iosTouchAction(driver, moveCoordinates);

            }
            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.pageXpath);
            if (responseData.isStatus()) {
                iosTouchAction(driver, moveCoordinates);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ResponseData();
        }
    }

    /**
     * 滑动页面
     *
     * @param driver
     * @param moveCoordinates
     */
    private void iosTouchAction(IOSDriver driver, MoveCoordinates moveCoordinates) {

        new IOSTouchAction(driver).press(PointOption.point(moveCoordinates.getOrginWith(), moveCoordinates.getOrginHeight())).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                .moveTo(PointOption.point(moveCoordinates.getMoveWidth(), moveCoordinates.getMoveHeight())).release().perform();
    }


    /**
     * 向上滑动页面
     *
     * @param driver
     * @param fields
     * @return
     */
    public ResponseData slidePageUp(IOSDriver driver, StfDevicesFields fields) {

        //获取屏幕的大小
        Dimension dimension = driver.manage().window().getSize();

        MoveCoordinates moveCoordinates = coordinatesUtil.getMoveCoordinates(dimension, 2);

        responseData = locationElement.iOSNsPredicateString(driver, fields, locationElement.getXCUIElementTypeStaticText("我的订单"));
        if (responseData.isStatus()) {

            iosTouchAction(driver, moveCoordinates);
        }

        return responseData;
    }


}
