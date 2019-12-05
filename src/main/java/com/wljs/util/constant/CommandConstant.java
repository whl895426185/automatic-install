package com.wljs.util.constant;

/**
 * 命令常量
 */
public class CommandConstant {

    //杀掉进程命令
    public static final String killProcessCommand = "adb shell am force-stop " + AndroidConfig.appPackage;
}
