package com.wljs.appium;

import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.CommandUtil;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 启动appium服务
 */
public class StartAppium {
    private Logger logger = LoggerFactory.getLogger(StartAppium.class);

    private CommandUtil commandUtil = new CommandUtil();

    private AppiumDriverLocalService service;


    /**
     * 启动Appium Server服务
     */
    public AppiumDriverLocalService startAppiumServer(StfDevicesFields fields) {
        boolean isServerRunning = checkIfServerIsRunnning(fields);
        if (isServerRunning) {
            return service;
        }


        int port = fields.getAppiumServerPort();

        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withIPAddress("127.0.0.1");
        builder.usingPort(port);
        builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "error");

        //Start the server with the builder
        service = AppiumDriverLocalService.buildService(builder);
        service.start();

        return service;
    }


    //linux服务
    /*public boolean checkIfServerIsRunnning(StfDevicesFields fields) {
        boolean isServerRunning = false;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(fields.getAppiumServerPort());
            serverSocket.close();
        } catch (IOException e) {
            isServerRunning = true;
        } finally {
            serverSocket = null;
        }
        return isServerRunning;
    }*/


    //mac os服务
    public boolean checkIfServerIsRunnning(StfDevicesFields fields) {
        boolean isServerRunning = false;

        String process = commandUtil.getProcess("lsof -i:" + fields.getAppiumServerPort(), fields);

        if (null != process && !process.equals("")) {
            isServerRunning = true;
        }

        return isServerRunning;
    }

}
