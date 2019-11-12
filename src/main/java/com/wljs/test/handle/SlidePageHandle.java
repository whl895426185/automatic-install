package com.wljs.test.handle;

import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * 执行UI自动化测试，滑动页面
 */
public class SlidePageHandle {
    private Logger logger = LoggerFactory.getLogger(SlidePageHandle.class);

    //向左滑动引导页
    public void slideGuidePage(AndroidDriver driver, int width, int height) throws InterruptedException {
        int orginWith = (new Double(width * 0.9)).intValue();
        int orginHeight = height / 2;
        int moveWidth = (new Double(width * 0.15)).intValue();
        int moveHeight = height / 2;

        logger.info("向左滑动：orginWith = " + orginWith + ", orginHeight = " + orginHeight + ", moveWidth = " + moveWidth + ", moveHeight = " + moveHeight);

        if (isAppear(driver, LabelConstant.slidePageOne, 2)) {
            new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                    .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();
        }
        if (isAppear(driver, LabelConstant.slidePageTwo, 2)) {
            new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                    .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();
        }
    }

    //向上滑动页面
    public String slidePageUp(AndroidDriver driver, int width, int height) {
        //向上滑动
        if (isAppear(driver, LabelConstant.mineBtnName, 1)) {
            logger.info("---------------模拟向上滑动页面，查看商品信息---------------");

            int orginWith = width / 2;
            int orginHeight = (new Double(height * 0.9)).intValue();
            int moveWidth = width / 2;
            int moveHeight = new Double(height * 0.1).intValue();

            logger.info("向上滑动：orginWith = " + orginWith + ", orginHeight = " + orginHeight + ", moveWidth = " + moveWidth + ", moveHeight = " + moveHeight);

            new TouchAction(driver).press(PointOption.point(orginWith, orginHeight)).waitAction(WaitOptions.waitOptions(Duration.ofSeconds(3)))
                    .moveTo(PointOption.point(moveWidth, moveHeight)).release().perform();

            return null;
        }else{
            driver.closeApp();
            logger.info("---------------点击登录按钮无法跳转页面，程序问题，默认安装成功--------------");
            logger.info("---------------测试用例执行完毕，关闭未来集市APP---------------");

            driver.quit();

            return "没有获取到元素： //*//*[@text='" + LabelConstant.mineBtnName + "']";
        }

    }

    private boolean isAppear(AndroidDriver driver, String text, int type) {
        WaitElementHandle elementHandle = new WaitElementHandle();
        return elementHandle.isAppear(driver, text, type);
    }
}
