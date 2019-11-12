package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
import com.wljs.util.constant.InstallStepConstant;
import com.wljs.util.constant.PhoneTypeConstant;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 华为手机,兼容机型：
 * 1. HUAWEI P30：型号是ELE-AL100
 * 2. HUAWEI P10：型号是VTR-AL00, 有安装步骤
 * 3. HUAWEI nova 4e: 型号是MAR-AL00, 有安装步骤
 * 4. HUAWEI nova3: 型号是PAR-AL00
 */
public class HuaweiStep extends ElementHandle {
    private Logger logger = LoggerFactory.getLogger(HuaweiStep.class);

    public String installStep(AndroidDriver driver, String deviceName) {
        String expection = null;
        try {
            logger.info("-----------------准备开始安装步骤-----------------");
            if (deviceName.contains(PhoneTypeConstant.HUAWEI_PHONE_MODEL1)
                    || deviceName.contains(PhoneTypeConstant.HUAWEI_PHONE_MODEL2)) {

                //步驟一
                expection = waitingElement(driver, InstallStepConstant.huawei_step_1);
                if (null != expection) {
                    return expection;
                }
                expection = tap(driver, InstallStepConstant.huawei_step_1, 1);
                if (null != expection) {
                    return expection;
                }

                //步驟二
                expection = waitingElement(driver, InstallStepConstant.huawei_step_2);
                if (null != expection) {
                    return expection;
                }
                expection = tap(driver, InstallStepConstant.huawei_step_2, 2);
                if (null != expection) {
                    return expection;
                }

                //步驟三
                expection = waitingElement(driver, InstallStepConstant.huawei_step_3);
                if (null != expection) {
                    return expection;
                }
                expection = tap(driver, InstallStepConstant.huawei_step_3, 3);
                if (null != expection) {
                    return expection;
                }
            }

        } catch (Exception e) {
            logger.info("安装过程中异常信息：" + e);

            expection = e.toString();
        } finally {
            return expection;
        }

    }

    /**
     * 获取元素坐标点
     *
     * @param driver
     * @param text   安裝步驟名稱
     * @param type   安裝步驟順序
     * @return
     */
    public String tap(AndroidDriver driver, String text, int type) {
        String keyword = null;
        if (1 == type) {
            keyword = "resource-id=\"android:id/button1\"";
        } else if (2 == type) {
            keyword = "resource-id=\"com.android.packageinstaller:id/ok_button\"";
        } else if (3 == type) {
            keyword = "resource-id=\"com.android.packageinstaller:id/done_button\"";
        }

        Map<String, Object> totalXYMap = getCoordinates(driver, keyword, text);
        String expection = String.valueOf(totalXYMap.get("expection"));
        if (null != expection) {
            return expection;
        }

        int totalX = Integer.valueOf(String.valueOf(totalXYMap.get("totalX")));
        int totalY = Integer.valueOf(String.valueOf(totalXYMap.get("totalY")));

        double x = totalX / 2;
        double y = totalY / 2;

        clickCoordinates(driver, x, y, text);
        return null;
    }
}
