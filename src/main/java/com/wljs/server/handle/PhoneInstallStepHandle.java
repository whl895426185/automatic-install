package com.wljs.server.handle;

import com.wljs.phonetype.*;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.constant.PhoneTypeConstant;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

public class PhoneInstallStepHandle {

    /**
     * 不同的机型调用不同的安装步骤
     *
     * @param fields
     * @param driver
     * @return
     */
    public ResponseData installStep(StfDevicesFields fields, AndroidDriver<AndroidElement> driver) {
        String deviceName = fields.getDeviceName();

        //OPPO R9s Plus
        if (deviceName.contains(PhoneTypeConstant.OPPO_PHONE)) {
            OppoStep oppo = new OppoStep();
            return oppo.installStep(driver, fields);

            //vivo X9
        } else if (deviceName.contains(PhoneTypeConstant.VIVO_PHONE)) {
            VivoStep vivo = new VivoStep();
            return vivo.installStep(driver, fields);

            //HUAWEI P30/HUAWEI nova3
        } else if (deviceName.contains(PhoneTypeConstant.HUAWEI_PHONE)) {
            HuaweiStep huawei = new HuaweiStep();
            return huawei.installStep(driver, fields);

            //MEIZU
        } else if (deviceName.contains(PhoneTypeConstant.MEIZU_PHONE)) {
            MeizuStep meizu = new MeizuStep();
            return meizu.installStep(driver, fields);

            //Samsung
        } else if (deviceName.contains(PhoneTypeConstant.SAMSUNG_PHONE)) {
            SamsungStep samsung = new SamsungStep();
            return samsung.installStep(driver, fields);
        }
        return null;
    }
}
