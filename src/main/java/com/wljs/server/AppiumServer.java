package com.wljs.server;

import com.wljs.util.constant.ConfigConstant;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 启动appium服务
 */
public class AppiumServer {
    private Logger logger = LoggerFactory.getLogger(AppiumServer.class);

    private AppiumDriverLocalService service;


    public AppiumDriverLocalService start(int port) {
        logger.info("-----------------传参端口【" + port + "】-----------------");
        if(checkIfServerIsRunnning(port)) {
            logger.info("-----------------Appium Server服务端口【" + port + "】已启动，无需再重启-----------------");
        } else {
            startAppiumServer(port);
//            stopAppiumServer(port);
        }
        return service;
    }
    /**
     * 启动Appium Server服务
     */
    public void startAppiumServer(int port) {
        logger.info("-----------------启动Appium Server服务端口【" + port + "】-----------------");
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("noReset", "false");
        cap.setCapability("noSign", "true");
        cap.setCapability("unicodeKeyboard", true);
        cap.setCapability("resetKeyboard", true);

        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withIPAddress(ConfigConstant.appiumIp);
        builder.usingPort(port);
        builder.withCapabilities(cap);
        builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
        builder.withArgument(GeneralServerFlag.LOG_LEVEL,"error");

        //Start the server with the builder
        service = AppiumDriverLocalService.buildService(builder);
        service.start();
    }

    /**
     * 停止Appium Server服务
     */
    public void stopAppiumServer(int port) {
        if(checkIfServerIsRunnning(port)) {
            service.stop();
        }
        logger.info("-----------------停止Appium Server服务端口【" + port + "】-----------------");

    }

    public boolean checkIfServerIsRunnning(int port) {
        boolean isServerRunning = false;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (IOException e) {
            isServerRunning = true;
        } finally {
            serverSocket = null;
        }
        return isServerRunning;
    }

    public static void main(String[] arg){
        AppiumServer server = new AppiumServer();
        server.startAppiumServer(4723);
        server.stopAppiumServer(4723);
    }
}
