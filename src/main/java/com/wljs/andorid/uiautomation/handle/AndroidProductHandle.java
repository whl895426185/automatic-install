package com.wljs.andorid.uiautomation.handle;

import com.wljs.andorid.common.LocationElement;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行UI自动化测试，获取商品名称和价格
 */
public class AndroidProductHandle {
    private Logger logger = LoggerFactory.getLogger(AndroidProductHandle.class);

    //定位元素
    private LocationElement locationElement = new LocationElement();

    private ResponseData responseData = new ResponseData();

    /**
     * 获取列表第一个商品名称和价格
     *
     * @return
     */
    public ResponseData productList(AndroidDriver driver, StfDevicesFields fields) {
        try {
            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.productNameForListXpath);
            if (responseData.isStatus()) {
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 商品列表获取第一个商品名称 = " + responseData.getWebElement().getText());
            }

            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.productPriceForListXpath);
            WebElement proPriceEle = null;
            if (responseData.isStatus()) {
                proPriceEle = responseData.getWebElement();
                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 商品列表获取第一个商品价格 = " + responseData.getWebElement().getText());
            }


            if (null != proPriceEle) {
                proPriceEle.click();

                responseData = productDetail(driver, fields);
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行商品列表UI自动化测试失败: " + e);

            responseData = new ResponseData(false, e, "执行商品列表UI自动化测试失败:" + e.getMessage());

        } finally {
            return responseData;
        }
    }

    /**
     * 点击进入商品详情，获取商品名称和价格
     *
     * @return
     */
    public ResponseData productDetail(AndroidDriver driver, StfDevicesFields fields) {

        try {

            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.productNameForDetailXpath);
            if (responseData.isStatus()) {
                logger.info(":::::::::::::::::商品详情获取商品名称 = " + responseData.getWebElement().getText());
            }

            responseData = locationElement.isAppearByXpath(driver, fields, locationElement.productPriceForDetailXpath);
            if (responseData.isStatus()) {
                logger.info(":::::::::::::::::商品详情获取商品价格 = " + responseData.getWebElement().getText());
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行商品详情UI自动化测试失败: " + e);

            responseData = new ResponseData(false, e, "执行商品详情UI自动化测试失败:" + e.getMessage());

        } finally {
            return responseData;
        }
    }

}
