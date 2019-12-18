package com.wljs.andorid.common;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取元素的标识定义
 */
public class LocationElement {
    private Logger logger = LoggerFactory.getLogger(LocationElement.class);


    //执行UI自动化的
    public String getResourceId(String id) {
        return "//*[@resource-id='com.sibu.futurebazaar:id/" + id + "']";
    }

    public String getTextView(String text) {
        return "//*[@class='android.widget.TextView'][@text='" + text + "']";
    }

    //执行安装
    public String getText(String text) {
        return "//*//*[@text='" + text + "']";
    }

    //弹框
    public static final String allowBtnName = "允许";

    //引导页1
    public static final String pageOneXpath = "//android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.LinearLayout[1]/android.view.View[1]";
    //引导页2
    public static final String pageTwoXpath = "//android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.LinearLayout[1]/android.view.View[2]";

    //列表商品名称
    public static final String productNameForListXpath = "//android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.view.ViewGroup[1]/android.widget.ScrollView[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.support.v7.widget.RecyclerView[1]/android.view.ViewGroup[1]/android.view.ViewGroup[1]/android.widget.TextView[1]";
    //列表商品价格
    public static final String productPriceForListXpath = "//android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.view.ViewGroup[1]/android.widget.ScrollView[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.support.v7.widget.RecyclerView[1]/android.view.ViewGroup[1]/android.view.ViewGroup[1]/android.widget.LinearLayout[1]/android.widget.TextView[2]";

    //普通商品详情名称
    public static final String productNameForDetailXpath = "//android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.widget.LinearLayout[1]/android.widget.RelativeLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.RelativeLayout[1]/android.widget.TextView[1]";
    //普通商品详情价格
    public static final String productPriceForDetailXpath = "//android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.widget.LinearLayout[1]/android.widget.RelativeLayout[1]/android.widget.LinearLayout[1]/android.view.ViewGroup[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.TextView[2]";



    /**
     * 显示等待元素出现
     *
     * @param driver
     * @param xpath
     * @return
     */
    public ResponseData isAppearByXpath(AndroidDriver driver, StfDevicesFields fields, String xpath) {

        return waitElement(driver, fields, xpath);

    }

    private ResponseData waitElement(AndroidDriver driver, StfDevicesFields fields, String xpath) {
        ResponseData responseData = new ResponseData();
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            By by = MobileBy.xpath(xpath);


            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));

            responseData.setWebElement(element);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 没有发现元素【" + xpath + "】");

            responseData = new ResponseData(false, e, "没有获取到元素：" + xpath);

        } finally {
            responseData.setFields(fields);
            return responseData;
        }
    }

    /**
     * 显示等待元素出现
     *
     * @param driver
     * @param text
     * @return
     */
    public ResponseData isAppearByText(AndroidDriver driver, StfDevicesFields fields, String text) {
        String xpath = "//*//*[@text='" + text + "']";

        return waitElement(driver, fields, xpath);

    }

}
