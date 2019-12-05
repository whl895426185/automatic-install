package com.wljs.server;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.config.StfConfig;
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

    public WebDriver chromeDriver() {
        WebDriver driver = null;
        try {
            //设置chrondriver的路径
            System.setProperty("webdriver.chrome.driver", StfConfig.chromeDriverPath);

            //初始化浏览器
            driver = new ChromeDriver();

            // 打开Stf平台
            driver.get(StfConfig.stfUrl);

            Thread.sleep(3000);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("打开STF平台异常：" + e);
        }
        return driver;
    }


    /**
     * occupancyResources
     * 占用已连接且闲余的设备
     */
    public List<StfDevicesFields> occupancyResources(List<StfDevicesFields> androidFieldsList, List<StfDevicesFields> iosFieldsList) throws InterruptedException {
        List<StfDevicesFields> fieldsList = new ArrayList<StfDevicesFields>();

        ResponseData responseData = new ResponseData();

        //打开STF平台
        WebDriver driver = chromeDriver();

        //登陆STF平台
        responseData = login(driver);
        if (!responseData.isStatus()) {
            responseData.setExMsg("模拟登录STF平台失败");
            logger.info(":::::::::::::::::模拟登录STF平台失败::::::::::::::::: ");

            return fieldsList;
        }

        //刷新
        driver.navigate().refresh();

        Thread.sleep(20000);


        //模拟点击安卓设备
        clickAndroidList(androidFieldsList, driver, fieldsList);


        //模拟点击ios设备
        clickAndroidList(iosFieldsList, driver, fieldsList);



        Thread.sleep(2000);

        driver.close();

        return fieldsList;

    }

    /**
     * 模拟点击设备
     *
     * @param fieldsList
     * @param driver
     * @param list
     * @return
     */
    private List<StfDevicesFields> clickAndroidList(List<StfDevicesFields> fieldsList, WebDriver driver, List<StfDevicesFields> list) throws InterruptedException {
        ResponseData responseData = new ResponseData();

        for (StfDevicesFields fields : fieldsList) {
            responseData = isAppear(driver, fields);

            if (!responseData.isStatus()) {
                continue;
            }
            Thread.sleep(8000);

            logger.info(":::::::::::::::::模拟点击设备：" + fields.getDeviceName() + ", 占用设备资源::::::::::::::::: ");

            driver.navigate().refresh();
            //点击设备会进入control， 需回到devices页面才可以
            responseData = isAppear(driver, null);

            if (!responseData.isStatus()) {
                continue;
            }

            list.add(fields);

            driver.navigate().refresh();
        }
        return list;
    }


    /**
     * 模拟登录
     *
     * @param driver
     */
    /**
     * 模拟登录
     *
     * @param driver
     */
    private ResponseData login(WebDriver driver) {
        ResponseData responseData = new ResponseData();
        String text = null;
        try {
            text = "//input[@name='username']";
            WebElement nameEm = isLoginAppear(driver, text);
            if (null != nameEm) {
                nameEm.click();
                Thread.sleep(2000);
                nameEm.sendKeys(StfConfig.stfName);

                text = "//input[@name='password']";
                WebElement passwdEm = isLoginAppear(driver, text);
                if (null != passwdEm) {
                    passwdEm.click();
                    Thread.sleep(2000);
                    passwdEm.sendKeys(StfConfig.stfPasswd);

                    text = "//input[@type='submit']";
                    WebElement loginEm = isLoginAppear(driver, text);
                    if (null != loginEm) {
                        loginEm.click();
                        Thread.sleep(10000);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::登录STF平台失败，无法定位元素：" + e);
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("登录STF平台失败，无法定位元素：" + text);
        } finally {
            return responseData;
        }

    }

    public WebElement isLoginAppear(WebDriver driver, String text) {
        By by = By.xpath(text);

        WebDriverWait wait = new WebDriverWait(driver, 50);
        WebElement em = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        return em;
    }

    public ResponseData isAppear(WebDriver driver, StfDevicesFields fields) {
        ResponseData responseData = new ResponseData();
        try {
            By by = null;
            if (null != fields) {
                by = By.cssSelector("li[id $='-" + fields.getSerial() + "']");
            } else {
                by = By.xpath("//*//*[@href='/#!/devices']");
            }

            WebDriverWait wait = new WebDriverWait(driver, 50);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
            element.click();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(":::::::::::::::::没有等到元素出现，打印异常: " + e);

            responseData.setStatus(false);
            responseData.setException(e);
            if (null != fields) {
                responseData.setExMsg("无法定位到元素： li[id $='-" + fields.getSerial() + "']");
            } else {
                responseData.setExMsg("无法定位到元素： //*//*[@href='/#!/devices']");
            }
            responseData.setFields(fields);
        } finally {
            return responseData;
        }
    }

    /**
     * 释放资源(每台设备都登录再关闭)
     */
    public void releaseResources(StfDevicesFields fields) throws InterruptedException {
        ResponseData responseData = new ResponseData();
        if (null == fields) {
            return;
        }
        WebDriver driver = chromeDriver();

        responseData = login(driver);
        if (!responseData.isStatus()) {
            responseData.setExMsg("模拟登录STF平台失败");
            logger.info(":::::::::::::::::模拟登录STF平台失败::::::::::::::::: ");
            return;
        }

        //刷新
        driver.navigate().refresh();
        Thread.sleep(20000);

        responseData = isAppear(driver, fields);
        if (!responseData.isStatus()) {
            return;
        }


        logger.info(":::::::::::::::::模拟点击设备：" + fields.getDeviceName() + ", 释放设备资源::::::::::::::::: ");

        driver.navigate().refresh();

        driver.quit();
        return;
    }

}
