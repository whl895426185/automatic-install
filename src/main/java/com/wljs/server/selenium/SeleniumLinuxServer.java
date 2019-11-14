package com.wljs.server.selenium;

import com.wljs.message.ChatbotSendMessageNotify;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.ConfigConstant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

/**
 * 利用Selenium调用浏览器，动态模拟浏览器事件
 */
public class SeleniumLinuxServer {
    private Logger logger = LoggerFactory.getLogger(SeleniumLinuxServer.class);

    private ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();

    public Map<String, Object> chromeDriver() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            ChromeDriverService service = null;
            System.setProperty("webdriver.chrome.driver", ConfigConstant.chromeDriverPath);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");//无界面参数
            options.addArguments("--no-sandbox");//禁用沙盒

            DesiredCapabilities chromerCap = DesiredCapabilities.chrome();
            chromerCap.setCapability(ChromeOptions.CAPABILITY, options);

            service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(ConfigConstant.chromeDriverPath)).usingAnyFreePort().build();
            service.start();

            WebDriver driver = new RemoteWebDriver(service.getUrl(), chromerCap);

            driver.get(ConfigConstant.stfUrl);// 打开指定的网站

            resultMap.put("WebDriver", driver);
            resultMap.put("ChromeDriverService", service);

            Thread.sleep(3000);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("安装部署失败，异常信息为：" + e);
        }
        return resultMap;
    }

    /**
     * occupancyResources
     * 占用已连接且闲余的设备
     */
    public List<StfDevicesFields> occupancy(List<StfDevicesFields> fieldsList) throws InterruptedException {
        List<StfDevicesFields> resultList = new ArrayList<StfDevicesFields>();
        WebDriver driver = null;
        ChromeDriverService service = null;

        ResponseData responseData = new ResponseData();
        SeleniumLinuxServer seleniumServer = new SeleniumLinuxServer();
        Map<String, Object> resultMap = seleniumServer.chromeDriver();

        driver = (WebDriver) resultMap.get("WebDriver");
        service = (ChromeDriverService) resultMap.get("ChromeDriverService");

        responseData = login(driver);
        if (!responseData.isStatus()) {
            responseData.setExMsg("模拟登录STF平台失败");
            logger.info("------------模拟登录STF平台失败--------------");

            sendMsg(responseData, driver, service);
            return resultList;
        }

        //刷新
        driver.navigate().refresh();

        Thread.sleep(20000);

        for (StfDevicesFields fields : fieldsList) {
            responseData = isAppear(driver, fields);

            if (!responseData.isStatus()) {
                sendMsg(responseData, driver, service);
                continue;
            }

            logger.info("------------模拟点击设备：" + fields.getDeviceName() + ", 占用设备资源--------------");

            driver.navigate().refresh();
            //点击设备会进入control， 需回到devices页面才可以
            responseData = isAppear(driver, null);

            if (!responseData.isStatus()) {
                sendMsg(responseData, driver, service);
                continue;
            }

            logger.info("------------模拟点击【设备按钮】回到设备列表页面--------------");

            resultList.add(fields);

            driver.navigate().refresh();
        }

        Thread.sleep(2000);

        driver.close();
        service.stop();

        return resultList;

    }

    private void sendMsg(ResponseData responseData, WebDriver driver, ChromeDriverService service) {
        ChatbotSendMessageNotify sendMessageNotify = new ChatbotSendMessageNotify();
        sendMessageNotify.sendMessage(responseData);

        driver.close();
        service.stop();

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
                nameEm.sendKeys(ConfigConstant.stfName);

                text = "//input[@name='password']";
                WebElement passwdEm = isLoginAppear(driver, text);
                if (null != passwdEm) {
                    passwdEm.click();
                    Thread.sleep(2000);
                    passwdEm.sendKeys(ConfigConstant.stfPasswd);

                    text = "//input[@type='submit']";
                    WebElement loginEm = isLoginAppear(driver, text);
                    if (null != loginEm) {
                        loginEm.click();
                        Thread.sleep(10000);
                    }
                }
            }
        } catch (Exception e) {
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
            logger.error("没有等到元素出现，打印异常: " + e);

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
    public void release(StfDevicesFields fields) throws InterruptedException {
        WebDriver driver = null;
        ChromeDriverService service = null;

        ResponseData responseData = new ResponseData();
        if (null == fields) {
            return;
        }
        SeleniumLinuxServer seleniumServer = new SeleniumLinuxServer();
        Map<String, Object> resultMap = seleniumServer.chromeDriver();

        driver = (WebDriver) resultMap.get("WebDriver");
        service = (ChromeDriverService) resultMap.get("ChromeDriverService");

        responseData = login(driver);
        if (!responseData.isStatus()) {
            responseData.setExMsg("模拟登录STF平台失败");
            logger.info("------------模拟登录STF平台失败--------------");

            sendMsg(responseData, driver, service);
            return;
        }

        //刷新
        driver.navigate().refresh();
        Thread.sleep(20000);

        responseData = isAppear(driver, fields);
        if (!responseData.isStatus()) {
            sendMsg(responseData, driver, service);
            return;
        }


        logger.info("------------模拟点击设备：" + fields.getDeviceName() + ", 释放设备资源--------------");

        driver.navigate().refresh();

        driver.quit();
        service.stop();
        return;
    }
}
