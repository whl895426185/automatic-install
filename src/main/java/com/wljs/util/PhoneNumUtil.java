package com.wljs.util;

import com.wljs.config.AppConfig;

public class PhoneNumUtil {

    /**
     * 获取手机号
     * @param phoneTailNumber
     * @return
     */
    public String[] getPhoneArray(int phoneTailNumber) {
        String phoneStr = AppConfig.initPhoneStr;

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


        return phoneArray;

    }
}
