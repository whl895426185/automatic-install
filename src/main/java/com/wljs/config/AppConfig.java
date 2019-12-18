package com.wljs.config;

/**
 * android配置
 */
public class AppConfig {

    //app包名
    public static final String appPackage = "com.sibu.futurebazaar";

    //appActivity名
//    public static final String appActivity = ".ui.SplashActivityNew";
//    public static final String appActivity = ".ui.MainActivity";
//    public static final String appActivity = ".ui.SplashActivityNew2";
    public static final String appActivity = ".ui.SplashAnimActivity";

    public static final String autoLaunch = "autoLaunch";


    //截屏存储文件目录
    public static final String screenshotUrl = "/usr/local/node/run/log/error/images/";

    //动态生成登录手机号，后三位数字，在java程序处理
    public static final String initPhoneStr = "1|5|2|0|0|0|0|0";

}
