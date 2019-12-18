package com.wljs.ios.uiautomation.handle;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行UI自动化测试，获取商品名称和价格
 */
public class IosProductHandle {
    private Logger logger = LoggerFactory.getLogger(IosProductHandle.class);

    private LocationElement locationElement = new LocationElement();

    private ResponseData responseData = new ResponseData();

    /**
     * 获取列表第一个商品名称和价格
     *
     * @return
     */
    public ResponseData productList(IOSDriver driver, StfDevicesFields fields) {
        boolean proNameFlag = true;
        boolean proPriceFlag = true;
        try {
            //商品名称

            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.productNameForListXpath);
            if (responseData.isStatus()) {
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 商品列表获取第一个商品名称 = " + responseData.getWebElement().getText());
            } else {
                proNameFlag = false;
            }

            //商品价格
            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.productPriceForListXpath);
            WebElement proPriceEle = null;
            if (responseData.isStatus()) {
                proPriceEle = responseData.getWebElement();
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 商品列表获取第一个商品价格 = " + responseData.getWebElement().getText());
            } else {
                proPriceFlag = false;
            }

            //进入商品详情
            if (null != proPriceEle) {
                proPriceEle.click();


                //商品详情
                responseData = productDetail(driver, fields);
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行商品列表UI自动化测试失败： " + e);

            if (!proNameFlag) {
                responseData = new ResponseData(false, e, "无法定位元素： " + locationElement.productNameForListXpath);
            } else if (!proPriceFlag) {
                responseData = new ResponseData(false, e, "无法定位元素： " + locationElement.productPriceForListXpath);
            }

        } finally {
            return responseData;
        }

    }

    /**
     * 点击进入商品详情，获取商品名称和价格
     *
     * @return
     */
    public ResponseData productDetail(IOSDriver driver, StfDevicesFields fields) {
        boolean proNameFlag = true;
        boolean proPriceFlag = true;
        try {
            //商品名称
            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.productNameForDetailXpath);
            if (responseData.isStatus()) {
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 商品详情获取商品名称 = " + responseData.getWebElement().getText());

            } else {
                proNameFlag = false;
            }


            //商品价格
            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.productPriceForDetailXpath);
            if (responseData.isStatus()) {
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 商品详情获取商品价格 = " + responseData.getWebElement().getText());

            } else {
                proPriceFlag = false;
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行商品详情UI自动化测试失败: " + e);

            if (!proNameFlag) {
                responseData = new ResponseData(false, e, "无法定位元素： " + locationElement.productNameForDetailXpath);
            } else if (!proPriceFlag) {
                responseData = new ResponseData(false, e, "无法定位元素： " + locationElement.productPriceForDetailXpath);
            }

        } finally {
            return responseData;
        }
    }

}
