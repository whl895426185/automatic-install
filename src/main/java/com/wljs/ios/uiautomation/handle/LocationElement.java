package com.wljs.ios.uiautomation.handle;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取元素的标识定义
 * <p>
 */
public class LocationElement {
    private Logger logger = LoggerFactory.getLogger(LocationElement.class);


    public String getXCUIElementTypeButton(String param) {
        return "type == 'XCUIElementTypeButton' AND label == '" + param + "'";
    }


    public String getXCUIElementTypeStaticText(String param) {
        return "type == 'XCUIElementTypeStaticText' AND label == '" + param + "'";
    }


    //输入框
    public String getXCUIElementTypeTextField(String param) {
        return "type == 'XCUIElementTypeTextField' AND value='" + param + "'";

    }

    //引导页
    public String pageXpath = "//XCUIElementTypeApplication[1]/XCUIElementTypeWindow[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypePageIndicator[1]";


    //列表商品名称
    public String productNameForListXpath = "//XCUIElementTypeApplication[1]/XCUIElementTypeWindow[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeCollectionView[1]/XCUIElementTypeCell[4]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]";


    //列表商品价格
    public String productPriceForListXpath = "//XCUIElementTypeApplication[1]/XCUIElementTypeWindow[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeCollectionView[1]/XCUIElementTypeCell[4]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[2]";


    //详情商品名称
    public String productNameForDetailXpath = "//XCUIElementTypeApplication[1]/XCUIElementTypeWindow[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeScrollView[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[2]/XCUIElementTypeOther[3]/XCUIElementTypeOther[1]";


    //详情商品价格
    public String productPriceForDetailXpath = "//XCUIElementTypeApplication[1]/XCUIElementTypeWindow[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeScrollView[1]/XCUIElementTypeOther[1]/XCUIElementTypeOther[2]/XCUIElementTypeOther[1]/XCUIElementTypeOther[1]/XCUIElementTypeStaticText[1]";


    /**
     * 显示等待元素出现
     * <p>
     * iOSNsPredicateString : iOS 谓词的定位方式，仅支持 XCTest 框架，需大于 iOS 9.3或以上
     *
     * @param driver
     * @param xpath
     * @return
     */
    public ResponseData iOSNsPredicateString(IOSDriver driver, StfDevicesFields fields, String xpath) {
        ResponseData responseData = new ResponseData();
        try {

            WebDriverWait wait = new WebDriverWait(driver, 10);


            By by = MobileBy.iOSNsPredicateString(xpath);

            WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(by));

            responseData.setWebElement(webElement);

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
     * <p>
     * 根据xpath直接定位
     *
     * @param driver
     * @param xpath
     * @return
     */
    public ResponseData isAppearByXpath(IOSDriver driver, StfDevicesFields fields, String xpath) {
        ResponseData responseData = new ResponseData();
        try {

            WebDriverWait wait = new WebDriverWait(driver, 10);


            By by = MobileBy.xpath(xpath);

            WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(by));

            responseData.setWebElement(webElement);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 没有发现元素【" + xpath + "】");

            responseData = new ResponseData(false, e, "没有获取到元素：" + xpath);

        } finally {
            responseData.setFields(fields);
            return responseData;
        }
    }

}
