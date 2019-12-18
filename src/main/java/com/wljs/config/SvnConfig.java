package com.wljs.config;

/**
 * svn配置
 */
public class SvnConfig {

    //定义访问svn仓库
    public static final String svnUrl = "svn://112.124.227.122:3690/wljs01";


    //访问svn登陆账号密码
    public static final String svnLoginName = "javaAdmin";
    public static final String svnLoginPwd = "123456";


    //定义apk/ipa包检出的本地文件目录
    public static final String filePath = "/usr/local/package/wljs01";

    public static final String versionFilePath = "/usr/local/package/wljs_install_txt/";

    /**
     * android
     */
    public static final String svnApkVersionTxtPath = versionFilePath + "android";

    //部署安裝包當前的版本
    public static final String apkVersionLogFileName = "apkVersionLog.txt";

    /**
     * ios
     */
    //定义ipa包检出的本地文件目录
    public static final String svnIpaVersionTxtPath = versionFilePath + "ios";

    //部署安裝包當前的版本
    public static final String ipaVersionLogFileName = "ipaVersionLog.txt";






}
