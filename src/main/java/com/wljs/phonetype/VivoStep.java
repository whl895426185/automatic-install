package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
import com.wljs.util.constant.InstallStepConstant;
import com.wljs.util.constant.PhoneTypeConstant;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * VIVO手机,兼容机型：
 * 1. VIVO NEX A：型号是NEX A
 *
 */
public class VivoStep extends ElementHandle{
    private Logger logger = LoggerFactory.getLogger(VivoStep.class);

    public boolean installStep(AndroidDriver driver, String deviceName) {
        boolean isSuccess = true;
        try {
            logger.info("-----------------准备开始安装步骤-----------------");
            //步驟一
            if (waitingElement(driver, InstallStepConstant.vivo_step_1)) {
                tap(driver, deviceName, InstallStepConstant.vivo_step_1, 1);

                //步驟二
                if (waitingElement(driver, InstallStepConstant.vivo_step_2)) {
                    tap(driver, deviceName, InstallStepConstant.vivo_step_2, 2);

                    //步驟三
                    if (waitingElement(driver, InstallStepConstant.vivo_step_3)) {
                        tap(driver, deviceName, InstallStepConstant.vivo_step_3, 3);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("安装过程中异常信息：" + e);
            isSuccess = false;
        } finally {
            return isSuccess;
        }
    }

    /**
     * 获取元素坐标点
     * @param driver
     * @param text 安裝步驟名稱
     * @param type 安裝步驟順序
     * @return
     */
    public void tap(AndroidDriver driver,String deviceName, String text, int type) {
        String keyword = null;

        if(1 == type){
            keyword = "resource-id=\"com.android.packageinstaller:id/continue_button\"";
        }else if(2 == type){
            keyword = "resource-id=\"com.android.packageinstaller:id/ok_button\"";
        }else if(3 == type){
            if(deviceName.contains(PhoneTypeConstant.VIVO_PHONE_MODEL1)){
                keyword = "resource-id=\"com.android.packageinstaller:id/cancel_button\"";
            }else{
                keyword = "resource-id=\"com.android.packageinstaller:id/done_button\"";
            }
        }

        Map<String, Object> totalXYMap = getCoordinates(driver, keyword, text);

        int totalX = Integer.valueOf(String.valueOf(totalXYMap.get("totalX")));
        int totalY = Integer.valueOf(String.valueOf(totalXYMap.get("totalY")));

        double x = totalX / 2;
        double y = totalY / 2;

        clickCoordinates(driver, x, y, text);
    }

}
