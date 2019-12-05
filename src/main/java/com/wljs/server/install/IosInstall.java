package com.wljs.server.install;


import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.CommandUtil;
import com.wljs.util.config.AppConfig;
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

        boolean isSuccess = true;

        //先执行卸载包
        String uninstallCmd = "ideviceinstaller -u " + fields.getSerial() + " -U " + AppConfig.appPackage;

        String uninstallResult = commandUtil.getProcess(uninstallCmd);

        logger.info("::::::::::::::::: <<<" + fields.getSerial() + ">>> 执行命令卸载包结果：" + uninstallResult);

        if (uninstallResult.contains("Complete")) {

            //执行安装包
            String installCmd = "ideviceinstaller -u " + fields.getSerial() + " -i " + path;

            String installResult = commandUtil.getProcess(installCmd);


            logger.info("::::::::::::::::: <<<" + fields.getSerial() + ">>> 执行命令安装包结果：" + installResult);

            if (!installResult.contains("Complete")) {
                isSuccess = false;
            }
        } else {
            isSuccess = false;
        }

        if (!isSuccess) {

            responseData.setStatus(false);
            responseData.setExMsg("自动部署安装失败");
            responseData.setFields(fields);

        }

        return responseData;
    }


}
