package com.wljs.andorid.install.phone;

import com.wljs.andorid.common.LocationElement;
import com.wljs.andorid.install.phone.handle.CoodinatesTap;
import com.wljs.andorid.install.phone.handle.PhoneSupplier;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
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
public class Huawei extends CoodinatesTap {
    private Logger logger = LoggerFactory.getLogger(Huawei.class);
    private LocationElement locationElement = new LocationElement();
    private ResponseData responseData = new ResponseData();

    public ResponseData installStep(AndroidDriver driver, StfDevicesFields fields) {

        if (fields.getDeviceName().contains(PhoneSupplier.HUAWEI_PHONE_MODEL1)
                || fields.getDeviceName().contains(PhoneSupplier.HUAWEI_PHONE_MODEL2)) {

            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行安装步骤！！！！");
            //步驟一
            responseData = locationElement.isAppearByText(driver, fields, locationElement.getText(huawei_step_1));
            if (!responseData.isStatus()) {
                return responseData;
            }
            responseData = tap(driver, fields, huawei_step_1, getHuaWeiKeyword1());
            if (!responseData.isStatus()) {
                return responseData;
            }

            //步驟二
            responseData = locationElement.isAppearByText(driver, fields, huawei_step_2);
            if (!responseData.isStatus()) {
                return responseData;
            }
            responseData = tap(driver, fields, huawei_step_2, getHuaWeiKeyword2());
            if (!responseData.isStatus()) {
                return responseData;
            }

            //步驟三
            responseData = locationElement.isAppearByText(driver, fields, huawei_step_3);
            if (!responseData.isStatus()) {
                return responseData;
            }
            responseData = tap(driver, fields, huawei_step_3, getHuaWeiKeyword3());
            if (!responseData.isStatus()) {
                return responseData;
            }
        }

        return responseData;

    }

}
