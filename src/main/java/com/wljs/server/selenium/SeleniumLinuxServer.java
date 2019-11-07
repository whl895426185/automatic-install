package com.wljs.server.selenium;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.ConfigConstant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
    public List<StfDevicesFields> occupancyResources(List<StfDevicesFields> fieldsList) {
        List<StfDevicesFields> resultList = new ArrayList<StfDevicesFields>();
        WebDriver driver = null;
        ChromeDriverService service = null;
        try {
            SeleniumLinuxServer seleniumServer = new SeleniumLinuxServer();
            Map<String, Object> resultMap = seleniumServer.chromeDriver();

            driver = (WebDriver) resultMap.get("WebDriver");
            service = (ChromeDriverService) resultMap.get("ChromeDriverService");

            boolean isSuccess = login(driver);
            if (!isSuccess) {
                logger.info("------------模拟登录STF平台失败--------------");
            }else{
                //刷新
                driver.navigate().refresh();

                Thread.sleep(20000);

                for (StfDevicesFields fields : fieldsList) {
                    if (isAppear(driver, fields.getSerial())) {
                        logger.info("------------模拟点击设备：" + fields.getSerial() + ", 占用设备资源--------------");

                        driver.navigate().refresh();

                        //点击设备会进入control， 需回到devices页面才可以
                        if (isAppear(driver, null)) {
                            logger.info("------------模拟点击【设备按钮】回到设备列表页面--------------");
                        }

                        resultList.add(fields);
                    }
                    driver.navigate().refresh();
                }
                Thread.sleep(2000);
            }


        } catch (Exception e) {
        } finally {
            driver.close();
            service.stop();
        }
        return resultList;
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
    private boolean login(WebDriver driver) throws InterruptedException {
        boolean isSuccess = true;
        WebElement nameEm = isLoginAppear(driver, "//input[@name='username']");
        if (null != nameEm) {
            nameEm.click();
            Thread.sleep(2000);
            nameEm.sendKeys(ConfigConstant.stfName);
            logger.info("输入账号： " + ConfigConstant.stfName);

            WebElement passwdEm = isLoginAppear(driver, "//input[@name='password']");
            if (null != passwdEm) {
                passwdEm.click();
                Thread.sleep(2000);
                passwdEm.sendKeys(ConfigConstant.stfPasswd);
                logger.info("输入密码： " + ConfigConstant.stfPasswd);


                WebElement loginEm = isLoginAppear(driver, "//input[@type='submit']");
                if (null != loginEm) {
                    loginEm.click();
                    Thread.sleep(10000);

                    logger.info("点击登录");
                } else {
                    isSuccess = false;
                }
            } else {
                isSuccess = false;
            }
        } else {
            isSuccess = false;
        }
        return isSuccess;
    }

    public WebElement isLoginAppear(WebDriver driver, String text) {
        WebElement em = null;
        try {
            By by = By.xpath(text);

            WebDriverWait wait = new WebDriverWait(driver, 50);
            em = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Exception e) {
            logger.error("没有等到元素出现，打印异常: " + e);
        }
        return em;
    }

    public boolean isAppear(WebDriver driver, String serial) {
        try {
            By by = null;
            if (null != serial) {
                by = By.cssSelector("li[id $='-" + serial + "']");
            } else {
                by = By.xpath("//*//*[@href='/#!/devices']");
            }

            WebDriverWait wait = new WebDriverWait(driver, 50);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
            element.click();
            return true;
        } catch (Exception e) {
            logger.error("没有等到元素出现，打印异常: " + e);
            return false;
        }
    }

    /**
     * 释放资源(每台设备都登录再关闭)
     */
    public void releaseResources(StfDevicesFields fields) {
        WebDriver driver = null;
        ChromeDriverService service = null;
        try {
            if (null == fields) {
                return;
            }
            SeleniumLinuxServer seleniumServer = new SeleniumLinuxServer();
            Map<String, Object> resultMap = seleniumServer.chromeDriver();

            driver = (WebDriver) resultMap.get("WebDriver");
            service = (ChromeDriverService) resultMap.get("ChromeDriverService");

            boolean isSuccess = login(driver);
            if (!isSuccess) {
                logger.info("------------模拟登录STF平台失败--------------");
            }else{
                //刷新
                driver.navigate().refresh();
                Thread.sleep(20000);

                if (isAppear(driver, fields.getSerial())) {
                    logger.info("------------模拟点击设备：" + fields.getSerial() + ", 释放设备资源--------------");
                }

                driver.navigate().refresh();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("安装部署失败，异常信息为：" + e);
        }finally {
            driver.quit();
            service.stop();
        }
    }
}
