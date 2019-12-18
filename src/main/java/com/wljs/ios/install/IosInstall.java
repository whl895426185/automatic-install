package com.wljs.ios.install;


import com.wljs.ios.uiautomation.IosUIAutomation;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.server.StartAppiumServer;
import com.wljs.util.CommandUtil;
import com.wljs.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安装ipa应用包
 */
public class IosInstall {
    private Logger logger = LoggerFactory.getLogger(IosInstall.class);

    private CommandUtil commandUtil = new CommandUtil();


    /**
     * 执行命令卸载，安装，appium自动安装ios应用包无法实现
     *
     * @param fields
     * @param path
     * @return
     */
    public ResponseData installApp(StfDevicesFields fields, String path) {

        ResponseData responseData = new ResponseData();
        String iosProcess = "";
        boolean isSuccess = false;
        try {
            //先执行卸载app
            String uninstallCmd = "ideviceinstaller -u " + fields.getSerial() + " -U " + AppConfig.appPackage;

            iosProcess = commandUtil.getProcess(uninstallCmd, fields);


            if (iosProcess.contains("Complete")) {
                logger.info("::::::::::::::::: <<<" + fields.getDeviceName() + ">>> 执行命令卸载包结果：成功");

                //执行安装app
                String installCmd = "ideviceinstaller -u " + fields.getSerial() + " -i " + path;

                iosProcess = commandUtil.getProcess(installCmd, fields);


                //检查app是否安装完毕
                int i = 0;
                do {
                    Thread.sleep(20000);

                    String checkCommand = "ideviceinstaller -l";

                    iosProcess = commandUtil.getProcess(checkCommand, fields);

                    if (iosProcess.contains(AppConfig.appPackage)) {
                        logger.info("::::::::::::::::: <<<" + fields.getDeviceName() + ">>> 执行命令安装包结果：成功");

                        isSuccess = true;
                    }
                    i++;
                } while (!isSuccess && i < 3);
            }

        } catch (Exception e) {

            e.printStackTrace();
            logger.error(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行自动部署安装失败：" + e);

            responseData = new ResponseData(false, e, "执行自动部署安装失败：" + e);

        } finally {
            if (!isSuccess) {

                responseData = new ResponseData(false, null, "执行自动部署安装失败", null, iosProcess);

            }
            responseData.setFields(fields);
            return responseData;
        }


    }


    public static void main(String[] arg) {
        StfDevicesFields fields = new StfDevicesFields();
        fields.setSerial("9251e39b63331e52538dc2cc122e72da3d652b41");
        fields.setVersion("13.2.3");
        fields.setModel(" iPhone 7 Plus");
        fields.setManufacturer("Apple");
        fields.setPlatform("iOS");
        fields.setAppiumServerPort(4723);
        fields.setSystemPort(8200);


        String apkPath = "/Volumes/Tools/test/2019-12-04-19-39-52.ipa";

        IosInstall install = new IosInstall();
        install.installApp(fields, apkPath);

        StartAppiumServer server = new StartAppiumServer();
        server.start(fields);

        IosUIAutomation uiTest = new IosUIAutomation();
        uiTest.uiAutomation(fields, 1);

    }

}
