package com.wljs.phonetype;

import com.wljs.phonetype.handle.ElementHandle;
import com.wljs.pojo.Coordinates;
import com.wljs.pojo.ResponseData;
import com.wljs.util.constant.InstallStepConstant;
import com.wljs.util.constant.PhoneTypeConstant;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 华为手机,兼容机型：
 * 1. HUAWEI P30：型号是ELE-AL100
 * 2. HUAWEI P10：型号是VTR-AL00, 有安装步骤
 * 3. HUAWEI nova 4e: 型号是MAR-AL00, 有安装步骤
 * 4. HUAWEI nova3: 型号是PAR-AL00
 */
public class HuaweiStep extends ElementHandle {
    private Logger logger = LoggerFactory.getLogger(HuaweiStep.class);

    public ResponseData installStep(AndroidDriver driver, String deviceName) {
        ResponseData responseData = new ResponseData();

        if (deviceName.contains(PhoneTypeConstant.HUAWEI_PHONE_MODEL1)
                || deviceName.contains(PhoneTypeConstant.HUAWEI_PHONE_MODEL2)) {

            logger.info("-----------------执行安装步骤！！！！-----------------");
            //步驟一
            responseData = waitingElement(driver, InstallStepConstant.huawei_step_1);
            if (!responseData.isStatus()) {
                return responseData;
            }
            responseData = tap(driver, InstallStepConstant.huawei_step_1, 1);
            if (!responseData.isStatus()) {
                return responseData;
            }

            //步驟二
            responseData = waitingElement(driver, InstallStepConstant.huawei_step_2);
            if (!responseData.isStatus()) {
                return responseData;
            }
            responseData = tap(driver, InstallStepConstant.huawei_step_2, 2);
            if (!responseData.isStatus()) {
                return responseData;
            }

            //步驟三
            responseData = waitingElement(driver, InstallStepConstant.huawei_step_3);
            if (!responseData.isStatus()) {
                return responseData;
            }
            responseData = tap(driver, InstallStepConstant.huawei_step_3, 3);
            if (!responseData.isStatus()) {
                return responseData;
            }
        }

        return responseData;

    }

    /**
     * 获取元素坐标点
     *
     * @param driver
     * @param text   安裝步驟名稱
     * @param type   安裝步驟順序
     * @return
     */
    public ResponseData tap(AndroidDriver driver, String text, int type) {
        ResponseData responseData = new ResponseData();
        String keyword = null;
        try {
            if (1 == type) {
                keyword = "resource-id=\"android:id/button1\"";
            } else if (2 == type) {
                keyword = "resource-id=\"com.android.packageinstaller:id/ok_button\"";
            } else if (3 == type) {
                keyword = "resource-id=\"com.android.packageinstaller:id/done_button\"";
            }

            Coordinates coordinates = getCoordinates(driver, keyword, text);

            int totalX = coordinates.getTotalX();
            int totalY = coordinates.getTotalY();

            double x = totalX / 2;
            double y = totalY / 2;

            clickCoordinates(driver, x, y, text);
        } catch (Exception e) {
            logger.error("无法通过关键字获取坐标信息： " + e);
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("无法通过关键字获取坐标信息： " + keyword);
        } finally {
            return responseData;
        }

    }
}
