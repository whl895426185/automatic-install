package com.wljs.server;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.server.install.AndroidInstall;
import com.wljs.server.install.IosInstall;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 部署安装包
 */
public class InstallServer {

    /**
     * 外部调用接口，执行部署
     *
     * @param queue
     * @param androidFile
     * @param iosFile
     * @return
     * @throws Exception
     */
    public ResponseData init(ArrayBlockingQueue queue, String androidFile, String iosFile) throws InterruptedException {
        StfDevicesFields fields = (StfDevicesFields) queue.take();

        //启动appium服务
        AppiumServer appiumServer = new AppiumServer();
        appiumServer.start(fields.getAppiumServerPort());

        //执行安装
        if (("Android").equals(fields.getPlatform())) {
            AndroidInstall androidInstall = new AndroidInstall();
            return androidInstall.installApp(fields, androidFile);

        } else if (("iOS").equals(fields.getPlatform())) {
            IosInstall iosInstall = new IosInstall();
            return iosInstall.installApp(fields, iosFile);
        }

        return new ResponseData();

    }


}
