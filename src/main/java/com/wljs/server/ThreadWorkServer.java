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
    private List<ResponseData> responseDataList;

    public ThreadWorkServer(CyclicBarrier cyclicBarrier, ArrayBlockingQueue queue, String apkPath, int phoneNum, String uuid, List<ResponseData> responseDataList) {
        this.cyclicBarrier = cyclicBarrier;
        this.queue = queue;
        this.apkPath = apkPath;
        this.phoneNum = phoneNum;
        this.uuid = uuid;
        this.responseDataList = responseDataList;
    }

    @Override
    public void run() {
        super.run();
        ResponseData responseData = new ResponseData();
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

                logger.info(":::::::::::::::::【" + responseData.getFields().getDeviceName() + "】线程" + Thread.currentThread().getName() + "执行完毕");

            }

            txtUtil.writeTxtFile(ConfigConstant.uuidPath,"::" + Thread.currentThread().getName(), uuid+".txt");
        } catch (Exception e) {
            logger.error(":::::::::::::::::【" + responseData.getFields().getDeviceName() + "】自动部署安装失败:" + e);
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
                responseData.setExMsg(":::::::::::::::::释放设备（" + responseData.getFields().getDeviceName() + "）资源失败");
            }finally {

                if(null == responseDataList || responseDataList.size() < 1){
                    responseDataList = new ArrayList<ResponseData>();
                }
                responseDataList.add(responseData);

                String txt = txtUtil.readTxtFile(ConfigConstant.uuidPath, uuid+".txt");

                int listSize = Integer.valueOf(txt.split("::")[1]);
                int length = txt.split("::").length - 2;
                if(listSize == length){

                    ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
                    messageNotify.sendMessage(responseDataList);
                    txtUtil.deleteTxtFile(ConfigConstant.uuidPath, uuid+".txt");
                }
            }
        }
    }
}
