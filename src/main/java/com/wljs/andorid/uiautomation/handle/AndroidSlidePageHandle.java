package com.wljs.andorid.uiautomation.handle;

import com.wljs.andorid.common.LocationElement;
import com.wljs.pojo.MoveCoordinates;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.MoveCoordinatesUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidTouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Dimension;

import java.time.Duration;

/**
 * 执行UI自动化测试，滑动页面
 */
public class AndroidSlidePageHandle {
    // private Logger logger = LoggerFactory.getLogger(SlidePageHandle.class);
    private ResponseData responseData = new ResponseData();

    //计算坐标
    private MoveCoordinatesUtil coordinatesUtil = new MoveCoordinatesUtil();
    //定位元素
    private LocationElement locationElement = new LocationElement();

    /**
     * 向左滑动引导页
     *
     * @param driver
     * @param fields
     * @return
     */
    public ResponseData slideGuidePage(AndroidDriver driver, StfDevicesFields fields) {

        //获取屏幕的大小
        Dimension dimension = driver.manage().window().getSize();
        MoveCoordinates moveCoordinates = coordinatesUtil.getMoveCoordinates(dimension, 1);

        try {

            Thread.sleep(2000);
            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.pageOneXpath);
            if (responseData.isStatus()) {
                androidTouchAction(driver, moveCoordinates);

            }
            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.pageTwoXpath);
            if (responseData.isStatus()) {

                androidTouchAction(driver, moveCoordinates);

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
    private void androidTouchAction(AndroidDriver driver, MoveCoordinates moveCoordinates) {

        new AndroidTouchAction(driver).press(PointOption.point(moveCoordinates.getOrginWith(), moveCoordinates.getOrginHeight())).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                .moveTo(PointOption.point(moveCoordinates.getMoveWidth(), moveCoordinates.getMoveHeight())).release().perform();
    }

    /**


    /**
     * 向上滑动页面
     *
     * @param driver
     * @param fields
     * @return
     */
    public ResponseData slidePageUp(AndroidDriver driver, StfDevicesFields fields) {
        //获取屏幕的大小
        Dimension dimension = driver.manage().window().getSize();

        MoveCoordinates moveCoordinates = coordinatesUtil.getMoveCoordinates(dimension, 2);

        responseData = locationElement.isAppearByXpath(driver, fields, locationElement.getTextView("我的订单"));
        if (responseData.isStatus()) {

            androidTouchAction(driver, moveCoordinates);
        }

        return responseData;
    }


}
