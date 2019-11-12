package com.wljs.server;

import com.wljs.message.ChatbotSendMessageNotify;
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
    private String expection = null;

    public ThreadWorkServer(CyclicBarrier cyclicBarrier, ArrayBlockingQueue queue, String apkPath, int phoneNum) {
        this.cyclicBarrier = cyclicBarrier;
        this.queue = queue;
        this.apkPath = apkPath;
        this.phoneNum = phoneNum;
    }

    @Override
    public void run() {
        super.run();
        StfDevicesFields fields = null;
        try {
            logger.info("-----------------" + Thread.currentThread().getName() + "开始等待其他线程-----------------");
            cyclicBarrier.await();

            logger.info("-----------------" + Thread.currentThread().getName() + "开始执行-----------------");

            InstallApkServer install = new InstallApkServer();
            fields = install.init(queue, apkPath);

            // 工作线程开始处理，这里用Thread.sleep()来模拟业务处理
            Thread.sleep(1000);

            expection = fields.getExpection();
            if (null == expection) {
                logger.info("-----------------检测是否安装成功，执行UI自动化测试-----------------");

                UIAutomationTest uiTest = new UIAutomationTest();
                expection = uiTest.executeTest(fields, apkPath, phoneNum);
                logger.info("-----------------" + Thread.currentThread().getName() + "执行完毕-----------------");
            }
        } catch (Exception e) {
            logger.error("自动部署安装失败：" + e);
            logger.info("-----------------自动部署安装失败-----------------");
            expection = e.toString();

        } finally {
            if (null != expection) {
                messageNotify.sendMessage("设备（" + fields.getDeviceName() + "）自动部署安装： 失败", expection);
            }

            //利用Selenium调用浏览器，动态模拟浏览器事件，释放设备资源
            SeleniumServer seleniumServer = new SeleniumServer();
            seleniumServer.releaseResources(fields);
        }
    }
}
