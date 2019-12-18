package com.wljs.server;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.config.StfConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 利用Selenium调用浏览器，动态模拟浏览器事件
 */
public class SeleniumServer {
    private Logger logger = LoggerFactory.getLogger(SeleniumServer.class);

    private ResponseData responseData = new ResponseData();


    /**
     * 模拟登录
     */
    private WebDriver loginSTF() {
        WebDriver driver = null;
        try {
            //设置chromedriver的路径
            System.setProperty("webdriver.chrome.driver", StfConfig.chromeDriverPath);

            //初始化浏览器
            driver = new ChromeDriver();

            // 打开STF平台
            driver.get(StfConfig.stfUrl);

            Thread.sleep(3000);

            responseData = isLoginAppearByXpath(driver, "username", 1);
            if (responseData.isStatus()) {

                responseData = isLoginAppearByXpath(driver, "password", 1);
                if (responseData.isStatus()) {

                    isLoginAppearByXpath(driver, null, 2);
                }
            }

            //刷新
            driver.navigate().refresh();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::登录STF平台失败：" + e);

        } finally {
            return driver;
        }

    }


    public ResponseData isLoginAppearByXpath(WebDriver driver, String name, int type) {
        ResponseData responseData = new ResponseData();
        String xpath = "";
        try {

            if (1 == type) {
                xpath = "//input[@name='" + name + "']";
            } else if (2 == type) {
                xpath = "//input[@type='submit']";
            }

            By by = By.xpath(xpath);

            WebDriverWait wait = new WebDriverWait(driver, 50);
            WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(by));

            webElement.click();

            if (1 == type) {
                if (name.equals("username")) {
                    webElement.sendKeys(StfConfig.stfName);
                } else if (name.equals("password")) {
                    webElement.sendKeys(StfConfig.stfPasswd);
                }
            }

            Thread.sleep(2000);


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::登录STF平台失败，无法定位元素：" + e);

            responseData = new ResponseData(false, e, "登录STF平台失败，无法定位元素：" + xpath);
        } finally {
            return responseData;
        }

    }


    /**
     * 占用已连接且闲余的设备，操作自动部署
     *
     * @param fieldsList
     * @return
     * @throws InterruptedException
     */
    public List<StfDevicesFields> occupancyResources(List<StfDevicesFields> fieldsList) {
        List<StfDevicesFields> resultFieldsList = new ArrayList<StfDevicesFields>();
        try {
            if (null == fieldsList || fieldsList.size() < 1) {
                return resultFieldsList;
            }
            //登陆STF
            WebDriver driver = loginSTF();

            //模拟点击安卓/ios设备
            for (StfDevicesFields fields : fieldsList) {
                responseData = isAppear(driver, "//button[@id='" + fields.getSerial() + "']");

                if (!responseData.isStatus()) {
                    continue;
                }
                Thread.sleep(2000);

                logger.info(":::::::::::::::::模拟点击设备：" + fields.getDeviceName() + ", 占用设备资源::::::::::::::::: ");

                driver.navigate().refresh();

                //点击设备会进入control， 需回到devices页面才可以
                responseData = isAppear(driver, "//*//*[@href='/#!/devices']");

                if (!responseData.isStatus()) {
                    continue;
                }

                resultFieldsList.add(fields);

                driver.navigate().refresh();
            }


            Thread.sleep(2000);

            driver.close();
            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultFieldsList;

    }


    public ResponseData isAppear(WebDriver driver, String xpath) {
        ResponseData responseData = new ResponseData();
        try {
            By by = By.xpath(xpath);

            WebDriverWait wait = new WebDriverWait(driver, 50);
            WebElement webElement = wait.until(ExpectedConditions.presenceOfElementLocated(by));

            webElement.click();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::没有等到元素出现，打印异常: " + e);

            responseData = new ResponseData(false, e, xpath);

        } finally {
            return responseData;
        }
    }

    /**
     * 释放资源(每台设备都登录再关闭)
     */
    public void releaseResources(List<StfDevicesFields> fieldsList) throws InterruptedException {
        if (null == fieldsList || fieldsList.size() < 1) {
            return;
        }

        //登陆STF平台
        WebDriver driver = loginSTF();

        Thread.sleep(20000);

        for (StfDevicesFields fields : fieldsList) {
            responseData = isAppear(driver, "//button[@id='" + fields.getSerial() + "']");

            if (responseData.isStatus()) {
                logger.info(":::::::::::::::::模拟点击设备：" + fields.getDeviceName() + ", 释放设备资源::::::::::::::::: ");

                driver.navigate().refresh();
            }
        }

        driver.close();
        driver.quit();
        return;
    }

}
