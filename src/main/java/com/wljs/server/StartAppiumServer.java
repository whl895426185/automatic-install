package com.wljs.server;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.CommandUtil;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 启动appium服务
 */
public class StartAppiumServer {
    private Logger logger = LoggerFactory.getLogger(StartAppiumServer.class);

    private CommandUtil commandUtil = new CommandUtil();

    private AppiumDriverLocalService service;


    public AppiumDriverLocalService start(StfDevicesFields fields) {

        boolean isServerRunning = checkIfServerIsRunnning(fields);

        if (isServerRunning) {
            return service;
        }
        startAppiumServer(fields);

        return service;
    }

    /**
     * 启动Appium Server服务
     */
    public void startAppiumServer(StfDevicesFields fields) {

        int port = fields.getAppiumServerPort();

        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("noReset", "false");
        cap.setCapability("noSign", "true");

        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withIPAddress("127.0.0.1");
        builder.usingPort(port);
        builder.withCapabilities(cap);
        builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "error");

        //Start the server with the builder
        service = AppiumDriverLocalService.buildService(builder);
        service.start();
    }

    /**
     * 停止Appium Server服务
     */
    public void stopAppiumServer(StfDevicesFields fields) {
        if (checkIfServerIsRunnning(fields)) {
            service.stop();
        }
        logger.info(":::::::::::::::::停止Appium Server服务端口【" + fields.getAppiumServerPort() + "】::::::::::::::::: ");

    }

    public boolean checkIfServerIsRunnning(StfDevicesFields fields) {

        boolean isServerRunning = false;

        String process = commandUtil.getProcess("lsof -i:" + fields.getAppiumServerPort(), fields);

        if (null != process && !process.equals("")) {
            isServerRunning = true;
        }

        return isServerRunning;
    }

}
