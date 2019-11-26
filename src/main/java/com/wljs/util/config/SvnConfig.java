package com.wljs.util.config;

/**
 * svn配置
 */
public class SvnConfig {

    //定义访问svn仓库目录地址
    public static final String svnFileUrl = "svn://112.124.227.122:3690/wljs01/apk";
    public static final String svnUserName = "javaAdmin";
    public static final String svnPassword = "123456";

    //定义apk包检出的本地文件目录
    public static final String localFilePath = "/home/tools/android-package/wljs01/apk";
    public static final String localApkVersionFilePath = "/home/tools/android-package";

    //本机测试
//    public static final String localFilePath = "D:\\apkPackage\\wljs01\\apk";
//    public static  final String localApkVersionFilePath = "D:\\apkPackage";

    //部署安裝包當前的版本
    public static final String apkVersionLogFileName = "apkVersionLog.txt";


}
