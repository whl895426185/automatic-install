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

    public boolean installStep(AndroidDriver driver, String deviceName) {
        boolean isSuccess = true;
        try {
            logger.info("-----------------准备开始安装步骤-----------------");
            if (deviceName.contains(PhoneTypeConstant.HUAWEI_PHONE_MODEL1)
                    || deviceName.contains(PhoneTypeConstant.HUAWEI_PHONE_MODEL2)) {

                //步驟一
                if (waitingElement(driver, InstallStepConstant.huawei_step_1)) {
                    tap(driver, InstallStepConstant.huawei_step_1, 1);

                    //步驟二
                    if (waitingElement(driver, InstallStepConstant.huawei_step_2)) {
                        tap(driver, InstallStepConstant.huawei_step_2, 2);

                        //步驟三
                        if (waitingElement(driver, InstallStepConstant.huawei_step_3)) {
                            tap(driver, InstallStepConstant.huawei_step_3, 3);
                        }
                    }
                }
            }
            isSuccess = true;

        } catch (Exception e) {
            logger.info("安装过程中异常信息：" + e);
            isSuccess = false;
        } finally {
            return isSuccess;
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
    public void tap(AndroidDriver driver, String text, int type) {
        String keyword = null;
        if (1 == type) {
            keyword = "resource-id=\"android:id/button1\"";
        } else if (2 == type) {
            keyword = "resource-id=\"com.android.packageinstaller:id/ok_button\"";
        } else if (3 == type) {
            keyword = "resource-id=\"com.android.packageinstaller:id/done_button\"";
        }

        Map<String, Object> totalXYMap = getCoordinates(driver, keyword, text);
        int totalX = Integer.valueOf(String.valueOf(totalXYMap.get("totalX")));
        int totalY = Integer.valueOf(String.valueOf(totalXYMap.get("totalY")));

        double x = totalX / 2;
        double y = totalY / 2;

        clickCoordinates(driver, x, y, text);
    }
}