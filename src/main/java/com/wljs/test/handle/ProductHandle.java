package com.wljs.test.handle;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行UI自动化测试，获取商品名称和价格
 */
public class ProductHandle {
    private Logger logger = LoggerFactory.getLogger(ProductHandle.class);

    /**
     * 获取列表第一个商品名称和价格
     *
     * @return
     */
    public ResponseData productList(AndroidDriver driver, StfDevicesFields fields) {
        ResponseData responseData = new ResponseData();
        String text = null;
        try {
            text = LabelConstant.productNameForList;
            WebElement namelistEm = driver.findElement(By.xpath(text));
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 商品列表获取第一个商品名称 = " + namelistEm.getText());

            text = LabelConstant.productPriceForList;
            WebElement pricelistEm = driver.findElement(By.xpath(text));
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 商品列表获取第一个商品价格 = " + pricelistEm.getText());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("无法定位元素： " + e);
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("无法定位元素： " + text);
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
        ResponseData responseData = new ResponseData();
        //商品有区分爆款和普通商品
        //普通商品详情，暂时屏蔽(樣式有變)
        try {
            driver.findElement(By.xpath(LabelConstant.productBtn)).click();
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 模拟点击第一个商品，进入商品详情");

//        WebElement namedetailEm = driver.findElement(By.xpath(LabelConstant.productNameForDetail));
//        logger.info(":::::::::::::::::商品详情获取商品名称 = " + namedetailEm.getText());


//        WebElement pricedetailEm = driver.findElement(By.xpath(LabelConstant.CommonProPriceForDetail));
//        logger.info(":::::::::::::::::商品详情获取商品价格 = " + pricedetailEm.getText());

//        if (("").equals(namedetailEm.getText())) {
//            logger.info(":::::::::::::::::商品详情获取商品名称，内容展示为空！！！！");
//        }
        /*if(("").equals(pricedetailEm.getText())){
            logger.info(":::::::::::::::::商品详情获取商品价格，内容展示为空！！！！");
        }*/
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行商品详情UI自动化测试失败: " + e);
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("执行商品详情UI自动化测试失败:" + e.getMessage());
        } finally {
            return responseData;
        }
    }

}
