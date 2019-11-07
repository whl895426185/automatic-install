package com.wljs.util.constant;

/**
 * 基本连接配置
 */
public class ConfigConstant {

    /**
     * svn配置
     */
    //定义访问svn仓库目录地址
    public static final String svnFileUrl = "svn://112.124.227.122:3690/wljs01/apk";
    public static final String svnUserName = "javaAdmin";
    public static final String svnPassword = "123456";

    //定义apk包检出的本地文件目录
    public static final String localFilePath = "/home/tools/android-package/wljs01/apk";
    public static  final String localApkVersionFilePath = "/home/tools/android-package";
//    public static final String localFilePath = "D:\\apkPackage\\wljs01\\apk";
//    public static  final String localApkVersionFilePath = "D:\\apkPackage";

    //截屏存储文件目录
    public static final String screenshotUrl = "/home/tools/android-package/screenshot_image/";

//    public static final String screenshotUrl = "D:\\apkPackage\\screenshot_image\\";

    /**
     * rethinkdb配置
     */
    public static final String rethinkdb_host = "192.168.88.16";
    public static final int rethinkdb_port = 28015;
    public static final String rethinkdb_dbName = "stf";
    public static final String rethinkdb_tableName = "devices";

    /**
     * android配置
     */
    public static final String appPackage = "com.sibu.futurebazaar";
    public static final String appActivity = ".ui.SplashActivityNew";
    public static final String platformName = "Android";
    public static final String autoLaunch = "autoLaunch";


    /**
     * stf配置
     */
    public static final String stfUrl = "http://192.168.88.16:7100/auth/mock/#!%2Fdevices";
    public static final String stfName = "deployer";
    public static final String stfPasswd = "123456";

    /**
     * chromedriver配置
     */
    // 定义 chrome driver驱动路径
    public static final String chromeDriverPath = "/usr/local/chromedriver/chromedriver";
    // chrome 路径
    public static final String driverPath = "/opt/google/chrome/chrome";


    /**
     * 钉钉配置
     * 参考这篇文章https://blog.csdn.net/u013412027/article/details/85264201，获取APPKEY和密钥
     */
    public final static String dd_appKey ="dinglctchvrhgb0tzkqk";
    public final static String dd_appSecret="CV9y5mqB1iN4mGH86CONWQZriGYY4wg8vlp4nMC5UXuqIy4llyj39uU26THaAhoR";
    public final static Long dd_agentId = 302866152l;
//    public final static String dd_corpId = "ding99f9ce33cd890ad735c2f4657eb6378f";
//    public final static String dd_SSOsecret = "2kufNK2RIWIWj81v7W7E3wug_JGOZnz0Tp_aw4wxzJn8CLGzhPb7thjEfXbvS7Ed";


    /**
     * appium服務配置
     *
     */
    public static final String appiumIp = "127.0.0.1";


    /**
     * 邮件配置
     */
    // 邮件服务器主机名,QQ邮箱的 SMTP 服务器地址为: smtp.qq.com
    public static final String myEmailSMTPHost = "smtp.qq.com";
    //端口号是465或者587
    public static final String myEmailSMTPPort = "465";
    //发件人邮箱
    public static final String myEmailAccount = "895426185@qq.com";
    //发件人昵称
    public static final String myEmailNickName = "AdminMonitor";
    //发件人邮箱密码（授权码）,在开启SMTP服务时会获取到一个授权码，把授权码填在这里
    public static final String myEmailPassword = "eaquwggwnibtbbcg";
    //郵件主題
    public static final String myEmailSubject = "自动部署邮件失败！！！！！！";
    //接收人邮箱
    public static final String toEmailAddress = "wanhuali@wljs.com";
    public static final String ccEmailAddress = "895426185@qq.com";
    //异常邮件绝对路径
//    public static final String errorLogPath = "D:\\log\\";
    public static final String errorLogPath = "/usr/local/node/run/log/";


    /**
     * 钉钉自定义机器人配置
     */
    public static String webHookToken = "https://oapi.dingtalk.com/robot/send?access_token=xxxxxx";
}
