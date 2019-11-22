package com.wljs.server;

import com.wljs.message.ChatbotSendMessageNotify;
import com.wljs.pojo.ResponseData;
import com.wljs.test.UIAutomationTest;
import com.wljs.util.TxtUtil;
import com.wljs.util.constant.ConfigConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CyclicBarrier;

public class ThreadWorkServer extends Thread {
    private Logger logger = LoggerFactory.getLogger(ThreadWorkServer.class);

    private CyclicBarrier cyclicBarrier;
    private ArrayBlockingQueue queue;
    private String apkPath;
    private int phoneNum;//始化手机号尾号，执行测试用例虚拟手机号用到
    private String uuid;//用来记录是否是同批执行的设备

    public ThreadWorkServer(CyclicBarrier cyclicBarrier, ArrayBlockingQueue queue, String apkPath, int phoneNum, String uuid) {
        this.cyclicBarrier = cyclicBarrier;
        this.queue = queue;
        this.apkPath = apkPath;
        this.phoneNum = phoneNum;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        super.run();
        ResponseData responseData = new ResponseData();
        List<ResponseData> responseDataList = new ArrayList<ResponseData>();
        TxtUtil txtUtil = new TxtUtil();
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

                txtUtil.writeTxtFile(ConfigConstant.uuidPath,"::" + Thread.currentThread().getName(), uuid+".txt");
            }

        } catch (Exception e) {
            logger.error("自动部署安装失败:" + e);
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("自动部署安装失败:" + e);
        } finally {

            try {
                //利用Selenium调用浏览器，动态模拟浏览器事件，释放设备资源
                SeleniumServer seleniumServer = new SeleniumServer();
                seleniumServer.releaseResources(responseData.getFields());

            } catch (Exception e) {
                responseData.setStatus(false);
                responseData.setException(e);
                responseData.setExMsg("释放设备（" + responseData.getFields().getDeviceName() + "）资源失败");
            }finally {

                responseDataList.add(responseData);

                ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
                String txt = txtUtil.readTxtFile(ConfigConstant.uuidPath, uuid+".txt");
                if(txt.split("::")[1].equals(txt.split("::").length - 2)){
                    if (null != responseDataList && responseDataList.size()>0) {
                        messageNotify.sendMessage(responseDataList);
                        txtUtil.deleteTxtFile(ConfigConstant.uuidPath, uuid+".txt");
                    }
                }
            }
        }
    }
}
