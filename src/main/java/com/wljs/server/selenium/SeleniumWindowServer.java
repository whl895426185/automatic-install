package com.wljs.server.selenium;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.ConfigConstant;
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
 * <p>
 * https://chromedriver.storage.googleapis.com/index.html
 * <p>
 * 或者
 * https://npm.taobao.org/mirrors/chromedriver/
 */
public class SeleniumWindowServer {
    private Logger logger = LoggerFactory.getLogger(SeleniumWindowServer.class);

    public Map<String, Object> chromeDriver() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
            WebDriver driver = new ChromeDriver();
            resultMap.put("ChromeDriver", driver);

            driver.get(ConfigConstant.stfUrl);// 打开指定的网站

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
    public List<StfDevicesFields> occupancy(List<StfDevicesFields> fieldsList) {
        List<StfDevicesFields> resultList = new ArrayList<StfDevicesFields>();
        ChromeDriver driver = null;
        try {
            SeleniumWindowServer seleniumServer = new SeleniumWindowServer();
            Map<String, Object> resultMap = seleniumServer.chromeDriver();

            driver = (ChromeDriver) resultMap.get("ChromeDriver");

            boolean isSuccess = login(driver);
            if (!isSuccess) {
                logger.info("------------模拟登录STF平台失败--------------");
            }else{

                //刷新
                driver.navigate().refresh();

                Thread.sleep(20000);

                for (StfDevicesFields fields : fieldsList) {

                    if (isAppear(driver, fields.getSerial())) {
                        logger.info("------------模拟点击设备：" + fields.getDeviceName() + ", 占用设备资源--------------");

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
        } catch (InterruptedException e) {
        } finally {
            if (null != driver) {
                driver.close();
            }
        }
        return resultList;
    }


    /**
     * 模拟登录
     *
     * @param driver
     */
    private boolean login(ChromeDriver driver) throws InterruptedException {
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

    public WebElement isLoginAppear(ChromeDriver driver, String text) {
        WebElement em = null;
        try {
            By by = By.xpath(text);

            WebDriverWait wait = new WebDriverWait(driver, 50);
            em = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Exception e) {
            logger.error("没有等到元素出现，打印异常: " + e.getMessage());
        }
        return em;
    }

    public boolean isAppear(ChromeDriver driver, String serial) {
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
            logger.error("没有等到元素出现，打印异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 释放资源(每台设备都登录再关闭)
     */
    public void release(StfDevicesFields fields) {
        ChromeDriver driver = null;
        try {
            if (null == fields) {
                return;
            }
            SeleniumWindowServer seleniumServer = new SeleniumWindowServer();
            Map<String, Object> resultMap = seleniumServer.chromeDriver();

            driver = (ChromeDriver) resultMap.get("ChromeDriver");

            boolean isSuccess = login(driver);
            if (!isSuccess) {
                logger.info("------------模拟登录STF平台失败--------------");
            }else{
                //刷新
                driver.navigate().refresh();
                Thread.sleep(20000);

                if (isAppear(driver, fields.getSerial())) {
                    logger.info("------------模拟点击设备：" + fields.getDeviceName() + ", 释放设备资源--------------");
                }

                driver.navigate().refresh();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("安装部署失败，异常信息为：" + e);
        }finally {
            driver.quit();
        }
    }

    public static void main(String[] arg) {

        SeleniumWindowServer window = new SeleniumWindowServer();

        List<StfDevicesFields> fieldsList = new ArrayList<>();
        StfDevicesFields fields = new StfDevicesFields();
        fields.setManufacturer("VIVO");
        fields.setModel(" X9");
        fields.setSerial("1d4bc416");
        fields.setVersion("7.1.2");
        fields.setAppiumServerPort(4723);
        fields.setSystemPort(8200);
        fieldsList.add(fields);

        window.occupancy(fieldsList);


        window.release(fields);

    }
}
