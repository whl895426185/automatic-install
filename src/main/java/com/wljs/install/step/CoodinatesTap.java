package com.wljs.install.step;

import com.wljs.pojo.Coordinates;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidTouchAction;
import io.appium.java_client.touch.offset.PointOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoodinatesTap {
    private Logger logger = LoggerFactory.getLogger(CoodinatesTap.class);

    private ScreenshotUtil screenshotUtil = new ScreenshotUtil();

    /**
     * oppo手机
     */
    public static final String oppo_step_1 = "安装";
    public static final String oppo_step_2_txt = "应用权限";
    public static final String oppo_step_2 = "安装";
    public static final String oppo_step_3 = "完成";

    public String getOppoKeyword1(String text) {
        return "class=\"android.widget.Button\" text=\"" + text + "\"";
    }

    public String getOppoKeyword2() {
        return "resource-id=\"com.android.packageinstaller:id/bottom_button_layout\"";
    }

    public String getOppoKeyword3() {
        return "com.android.packageinstaller:id/bottom_button_one";
    }


    /**
     * 华为手机
     */
    public static final String huawei_step_1 = "继续安装";
    public static final String huawei_step_2 = "继续安装";
    public static final String huawei_step_3 = "完成";

    public String getHuaWeiKeyword1() {
        return "resource-id=\"android:id/button1\"";
    }

    public String getHuaWeiKeyword2() {
        return "resource-id=\"com.android.packageinstaller:id/ok_button\"";
    }

    public String getHuaWeiKeyword3() {
        return "resource-id=\"com.android.packageinstaller:id/done_button\"";
    }


    /**
     * vivo手机
     */

    public static final String vivo_step_0 = "需要您验证身份后安装";
    public static final String vivo_step_0_1 = "确定";
    public static final String vivo_step_1 = "继续安装";
    public static final String vivo_step_2 = "安装";
    public static final String vivo_step_3 = "完成";

    public String getVivoKeyword1() {
        return "resource-id=\"com.android.packageinstaller:id/continue_button\"";
    }

    public String getVivoKeyword2() {
        return "resource-id=\"com.android.packageinstaller:id/ok_button\"";
    }

    public String getVivoKeyword3(String deviceName) {
        if (deviceName.contains(Supplier.VIVO_PHONE_MODEL1)) {
            return "resource-id=\"com.android.packageinstaller:id/cancel_button\"";
        } else {
            return "resource-id=\"com.android.packageinstaller:id/done_button\"";
        }
    }


    /**
     * 获取元素坐标点
     *
     * @param driver
     * @return
     */
    public ResponseData tap(AndroidDriver driver, StfDevicesFields fields, String text, String keyword) {
        ResponseData responseData = new ResponseData();
        try {

            Coordinates coordinates = getCoordinates(driver, keyword, text);

            int totalX = coordinates.getTotalX();
            int totalY = coordinates.getTotalY();

            double x = getX(totalX, text);
            double y = getY(totalY, text);


            clickCoordinates(driver, fields, x, y, text);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 获取坐标信息异常：" + e);

            String screenImg = screenshotUtil.screenshot(driver, null, fields.getSerial());

            responseData = new ResponseData(false, e, "无法通过关键字获取坐标信息： " + keyword, screenImg);

        } finally {
            return responseData;
        }
    }

    private double getY(int totalY, String text) {
        if (text.equals(oppo_step_1)) {
            return totalY / 2;

        } else if (text.equals(oppo_step_2)) {
            return totalY / 2;

        } else if (text.equals(oppo_step_3)) {
            return totalY * 0.5;

        } else if (text.equals(huawei_step_1) || text.equals(huawei_step_2) || text.equals(huawei_step_3)) {
            return totalY / 2;

        } else if (text.equals(vivo_step_0_1) || text.equals(vivo_step_1) || text.equals(vivo_step_2) || text.equals(vivo_step_3)) {
            return totalY / 2;
        }

        return 0;
    }

    private double getX(int totalX, String text) {
        if (text.equals(oppo_step_1)) {
            return totalX / 2;

        } else if (text.equals(oppo_step_2)) {
            return totalX * 0.75;

        } else if (text.equals(oppo_step_3)) {
            return totalX / 2;

        } else if (text.equals(huawei_step_1) || text.equals(huawei_step_2) || text.equals(huawei_step_3)) {
            return totalX / 2;

        } else if (text.equals(vivo_step_0_1) || text.equals(vivo_step_1) || text.equals(vivo_step_2) || text.equals(vivo_step_3)) {
            return totalX / 2;
        }

        return 0;

    }

    /**
     * 获取元素坐标
     *
     * @param driver
     * @param keyword 关键字
     * @param text    安装步骤名称
     * @return
     */
    public Coordinates getCoordinates(AndroidDriver driver, String keyword, String text) {
        Coordinates coordinates = new Coordinates();
        String xmlStr = driver.getPageSource();

        if (!xmlStr.contains(text)) {
            return null;
        }
        xmlStr = xmlStr.split(keyword)[1];
        xmlStr = xmlStr.split("bounds=\"")[1];
        xmlStr = xmlStr.substring(0, xmlStr.lastIndexOf("]"));
        xmlStr = xmlStr.replace("][", ",").replace("[", "");

        String[] bounsArray = xmlStr.split(",");

        coordinates.setMinX(Integer.valueOf(bounsArray[0]));
        coordinates.setMinY(Integer.valueOf(bounsArray[1]));
        coordinates.setMaxX(Integer.valueOf(bounsArray[2]));
        coordinates.setMaxY(Integer.valueOf(bounsArray[3]));

        coordinates.setTotalX(coordinates.getMinX() + coordinates.getMaxX());
        coordinates.setTotalY(coordinates.getMinY() + coordinates.getMaxY());

        return coordinates;
    }


    /**
     * 点击坐标
     *
     * @param driver
     * @param x      横坐標
     * @param y      纵坐标
     * @param text   安装步骤名称
     */
    public void clickCoordinates(AndroidDriver driver, StfDevicesFields fields, double x, double y, String text) {
        int resultX = new Double(x).intValue();
        int resultY = new Double(y).intValue();

        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: X坐标为：" + resultX + ", Y坐标为：" + resultY);
        logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击元素【" + text + "】按钮");

        AndroidTouchAction t = new AndroidTouchAction(driver);//模拟触摸点击
        t.tap(PointOption.point(resultX, resultY)).perform().release();
    }
}
