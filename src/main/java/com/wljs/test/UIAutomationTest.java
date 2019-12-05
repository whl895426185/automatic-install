package com.wljs.test;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;

public class UIAutomationTest{


    /**
     * 執行UI自动化测试
     *
     * @param fields      设备对象信息
     * @param androidFile apk文件绝对路径
     * @param iosFile     ipa文件绝对路径
     * @param phoneNum    手机尾号
     * @throws Exception
     */
    public ResponseData executeTest(StfDevicesFields fields, String androidFile, String iosFile, int phoneNum) {


        //执行安装
        if (("Android").equals(fields.getPlatform())) {
            AndroidUIAutomation androidUIAutomation = new AndroidUIAutomation();
            return androidUIAutomation.uiAutomation(fields, androidFile, phoneNum);

        } else if (("iOS").equals(fields.getPlatform())) {
            IosUIAutomation iosUIAutomation = new IosUIAutomation();
            return iosUIAutomation.uiAutomation(fields, iosFile, phoneNum);
        }

        return new ResponseData();


    }


}
