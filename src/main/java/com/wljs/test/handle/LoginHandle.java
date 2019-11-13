package com.wljs.test.handle;

import com.wljs.util.constant.InstallStepConstant;
import com.wljs.util.constant.LabelConstant;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行UI自动化测试，模拟登录
 */
public class LoginHandle {
    private Logger logger = LoggerFactory.getLogger(LoginHandle.class);

    public void login(AndroidDriver driver, int phoneTailNumber) {
        try {
            if (isAppear(driver, LabelConstant.smsVerifiyCodeBtnName)) {
                //填写手机号
                pressKeyPhone(driver, phoneTailNumber);

                //填写验证码
                driver.findElement(By.xpath(LabelConstant.verifiyCodeField)).click();
                pressKeyVerificationCode(driver);

                boolean isSuccess = true;
                try {
                    driver.hideKeyboard();
                } catch (Exception e) {
                    isSuccess = false;
                } finally {
                    isSuccess = true;
                }

                //点击【登录】按钮
                if (isSuccess) {
                    if (isAppear(driver, LabelConstant.loginBtnName)) {
                        driver.findElement(By.xpath(LabelConstant.loginBtn)).click();
                        logger.info("---------------模拟点击【登录】按钮--------------");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAppear(AndroidDriver driver, String text) {
        WaitElementHandle elementHandle = new WaitElementHandle();
        return elementHandle.isAppear(driver, text, 1);
    }

    /**
     * 填写手机号
     *
     * @param driver
     */
    private void pressKeyPhone(AndroidDriver driver, int phoneTailNumber) {
        String phoneStr = InstallStepConstant.phoneStr;

        //尾号
        String numStr = String.valueOf(phoneTailNumber);
        if (numStr.length() == 1) {
            phoneStr += ("|0|0|" + numStr);
        } else if (numStr.length() == 2) {
            phoneStr += ("|0|" + numStr.substring(1, numStr.length() - 1));
            phoneStr += ("|" + numStr.substring(1, numStr.length()));
        } else if (numStr.length() == 3) {
            phoneStr += ("|" + numStr.substring(0, numStr.length() - 2));
            phoneStr += ("|" + numStr.substring(1, numStr.length() - 1));
            phoneStr += ("|" + numStr.substring(2, numStr.length()));
        }

        String[] phoneArray = phoneStr.split("|");

        String result = "";
        for (String str : phoneArray) {
            if (str.equals("|")) {
                continue;
            }
            driver.pressKey(new KeyEvent(getKey(str)));
            result += str;
        }
        logger.info("---------------模拟输入手机号 = " + result + "---------------");
    }

    private AndroidKey getKey(String numStr) {
        int result = Integer.valueOf(numStr);
        if (0 == result) {
            return AndroidKey.DIGIT_0;
        } else if (1 == result) {
            return AndroidKey.DIGIT_1;
        } else if (2 == result) {
            return AndroidKey.DIGIT_2;
        } else if (3 == result) {
            return AndroidKey.DIGIT_3;
        } else if (4 == result) {
            return AndroidKey.DIGIT_4;
        } else if (5 == result) {
            return AndroidKey.DIGIT_5;
        } else if (6 == result) {
            return AndroidKey.DIGIT_6;
        } else if (7 == result) {
            return AndroidKey.DIGIT_7;
        } else if (8 == result) {
            return AndroidKey.DIGIT_8;
        } else if (9 == result) {
            return AndroidKey.DIGIT_9;
        }
        return null;
    }


    /**
     * 填写验证码
     *
     * @param driver
     */
    private void pressKeyVerificationCode(AndroidDriver driver) {
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        logger.info("---------------模拟输入验证码 = 888888---------------");
    }

    //新账号需要填写邀请码
    public void inviteCode(AndroidDriver driver) {
        if (isAppear(driver, LabelConstant.confirmInviteCodeBtnName)) {
            //填写邀请码
            pressKeyInvitationCode(driver);

            //点击【确定邀请码】按钮
            driver.findElement(By.xpath(LabelConstant.loginBtn)).click();
            logger.info("---------------模拟点击【确定邀请码】按钮--------------");

            //等待元素出現
            if (isAppear(driver, LabelConstant.confirmAndBindLoginBtnName)) {
                //点击【确认绑定并登录】按钮
                driver.findElement(By.xpath(LabelConstant.loginBtn)).click();

                logger.info("---------------模拟点击【确认绑定并登录】按钮--------------");
            }
        }
    }

    /**
     * 填写邀请码
     *
     * @param driver
     */
    private void pressKeyInvitationCode(AndroidDriver driver) {
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_6));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_0));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_0));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_0));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_0));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_6));
        logger.info("---------------模拟输入邀请码 = 600006---------------");
    }

}
