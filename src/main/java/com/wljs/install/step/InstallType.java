package com.wljs.install.step;

import com.wljs.install.step.phone.*;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

public class InstallType {



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
        if (deviceName.contains(Supplier.OPPO_PHONE)) {
            Oppo oppo = new Oppo();
            return oppo.installStep(driver, fields);

            //vivo X9
        } else if (deviceName.contains(Supplier.VIVO_PHONE)) {
            Vivo vivo = new Vivo();
            return vivo.installStep(driver, fields);

            //HUAWEI P30/HUAWEI nova3
        } else if (deviceName.contains(Supplier.HUAWEI_PHONE)) {
            Huawei huawei = new Huawei();
            return huawei.installStep(driver, fields);

            //MEIZU
        } else if (deviceName.contains(Supplier.MEIZU_PHONE)) {
            Meizu meizu = new Meizu();
            return meizu.installStep(driver, fields);

            //Samsung
        } else if (deviceName.contains(Supplier.SAMSUNG_PHONE)) {
            Samsung samsung = new Samsung();
            return samsung.installStep(driver, fields);
        }
        return null;
    }
}
