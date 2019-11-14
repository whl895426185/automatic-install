package com.wljs.server;

import com.wljs.message.ChatbotSendMessageNotify;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.test.UIAutomationTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CyclicBarrier;

public class ThreadWorkServer extends Thread {
    private Logger logger = LoggerFactory.getLogger(ThreadWorkServer.class);

    private CyclicBarrier cyclicBarrier;
    private ArrayBlockingQueue queue;
    private String apkPath;
    private int phoneNum;//始化手机号尾号，执行测试用例虚拟手机号用到

    private ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();

    public ThreadWorkServer(CyclicBarrier cyclicBarrier, ArrayBlockingQueue queue, String apkPath, int phoneNum) {
        this.cyclicBarrier = cyclicBarrier;
        this.queue = queue;
        this.apkPath = apkPath;
        this.phoneNum = phoneNum;
    }

    @Override
    public void run() {
        super.run();
        ResponseData responseData = null;
        try {
            cyclicBarrier.await();

            InstallApkServer install = new InstallApkServer();
            responseData = install.init(queue, apkPath);

            if (responseData.isStatus()) {

                // 工作线程开始处理，这里用Thread.sleep()来模拟业务处理
                Thread.sleep(1000);

                UIAutomationTest uiTest = new UIAutomationTest();
                responseData = uiTest.executeTest(responseData.getFields(), apkPath, phoneNum);
                logger.info("-----------------" + Thread.currentThread().getName() + "执行完毕-----------------");
            }

        } catch (Exception e) {
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("自动部署安装失败");
        } finally {
            messageNotify.sendMessage(responseData);

            //利用Selenium调用浏览器，动态模拟浏览器事件，释放设备资源
            SeleniumServer seleniumServer = new SeleniumServer();
            try {
                seleniumServer.releaseResources(responseData.getFields());
            } catch (InterruptedException e) {
                e.printStackTrace();
                responseData.setStatus(false);
                responseData.setException(e);
                responseData.setExMsg("释放设备（" + responseData.getFields().getDeviceName() + "）资源失败");
                messageNotify.sendMessage(responseData);
            }
        }
    }
}
