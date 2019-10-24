package com.wljs.server;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.ConfigConstant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利用Selenium调用浏览器，动态模拟浏览器事件
 */
public class SeleniumServer {
    private Logger logger = LoggerFactory.getLogger(SeleniumServer.class);

    public Map<String, Object> chromeDriver() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            /*******************************window*******************************************/
//            System.setProperty("webdriver.chrome.driver", "C:\\Users\\EDZ\\AppData\\Roaming\\npm\\node_modules\\appium\\node_modules\\appium-chromedriver\\chromedriver\\win\\chromedriver.exe");
//            ChromeDriver driver = new ChromeDriver();
//            resultMap.put("ChromeDriver", driver);
            /*******************************window*******************************************/

            /*******************************linux*******************************************/
            System.setProperty("webdriver.chrome.driver", ConfigConstant.chromeDriverPath);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");//无界面参数
            options.addArguments("--no-sandbox");//禁用沙盒

            DesiredCapabilities chromerCap = DesiredCapabilities.chrome();
            chromerCap.setCapability(ChromeOptions.CAPABILITY, options);

            ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(ConfigConstant.chromeDriverPath)).usingAnyFreePort().build();
            service.start();

            WebDriver driver = new RemoteWebDriver(service.getUrl(), chromerCap);
            /*******************************linux*******************************************/

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
     * 占用已连接且闲余的设备
     */
    public void occupancyResources(List<StfDevicesFields> fieldsList) {
        WebDriver driver = null;
        ChromeDriverService service = null;
        try {
            SeleniumServer seleniumServer = new SeleniumServer();
            Map<String, Object> resultMap = seleniumServer.chromeDriver();

            driver = (WebDriver) resultMap.get("WebDriver");
            service = (ChromeDriverService) resultMap.get("ChromeDriverService");

            login(driver);
            //刷新
            driver.navigate().refresh();

            Thread.sleep(10000);

            for (StfDevicesFields fields : fieldsList) {
                logger.info("------------模拟点击STF平台上的设备：" + fields.getSerial() + "--------------");
                WebDriverWait wait = new WebDriverWait(driver, 50, 1);
                WebElement em = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@href='#!/control/" + fields.getSerial() + "']")));
                em.click();

                //点击设备会进入control， 需回到devices页面才可以
                WebDriverWait wait2 = new WebDriverWait(driver, 50, 1);
                WebElement em2 = wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@href='/#!/devices']")));
                em2.click();

                driver.navigate().refresh();
            }
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            logger.error("安装部署失败，异常信息为：" + e);
        } finally {
            driver.close();
            service.stop();
        }
    }

    /**
     * 模拟登录
     * @param driver
     */
    private void login(WebDriver driver) {
        WebElement nameEm = driver.findElement(By.cssSelector("input[name=username]"));
        nameEm.sendKeys(ConfigConstant.stfName);

        WebElement emailEm = driver.findElement(By.cssSelector("input[name=email]"));
        emailEm.sendKeys(ConfigConstant.stfEmail);

        WebElement loginEm = driver.findElement(By.cssSelector("input[type='submit'][value='Log In']"));
        loginEm.click();
    }

    /**
     * 释放资源(每台设备都登录再关闭)
     */
    public void releaseResources(StfDevicesFields fields) {
        try {
            if(null == fields){
                return;
            }
            SeleniumServer seleniumServer = new SeleniumServer();
            Map<String, Object> resultMap = seleniumServer.chromeDriver();

            WebDriver driver = (WebDriver) resultMap.get("WebDriver");
            ChromeDriverService service = (ChromeDriverService) resultMap.get("ChromeDriverService");

            login(driver);

            //刷新
            driver.navigate().refresh();
            Thread.sleep(5000);

            driver.navigate().refresh();

            WebElement devicesEm = driver.findElement(By.xpath("//*[@href='#!/control/" + fields.getSerial() + "']"));
            devicesEm.click();

            driver.navigate().refresh();

            driver.quit();
            service.stop();

        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("安装部署失败，异常信息为：" + e);
        }
    }

    /*public static void main(String[] arg) {
        SeleniumServer seleniumServer = new SeleniumServer();

        StfDevicesServer stfDevicesServer = new StfDevicesServer();
        List<StfDevicesFields> fieldsList = stfDevicesServer.getFilesList();
        seleniumServer.occupancyResources(fieldsList);
        seleniumServer.releaseResources(fieldsList.get(0));
    }*/
}
