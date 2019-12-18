package com.wljs.server;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.andorid.uiautomation.AndroidUIAutomation;
import com.wljs.ios.uiautomation.IosUIAutomation;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

public class OperateThreadServer implements Callable {
    private Logger logger = LoggerFactory.getLogger(OperateThreadServer.class);

    private ArrayBlockingQueue queue;
    private String androidFile;//apk包绝对路径
    private String iosFile;//ipa包绝对路径

    private int phoneNum;//始化手机号尾号，执行测试用例虚拟手机号用到


    public OperateThreadServer(ArrayBlockingQueue queue, String androidFile, String iosFile, int phoneNum) {
        this.queue = queue;
        this.androidFile = androidFile;
        this.iosFile = iosFile;
        this.phoneNum = phoneNum;
    }

    @Override
    public ResponseData call() throws Exception {
        ResponseData responseData = new ResponseData();
        try {

            logger.info("::::::::::::::::: 线程" + Thread.currentThread().getName() + "开始执行");

            InstallServer install = new InstallServer();
            responseData = install.init(queue, androidFile, iosFile);

            logger.info(":::::::::::::::::<<<" + responseData.getFields().getDeviceName() + ">>>::::::::::::::::: 执行自动部署安装："
                    + (responseData.isStatus() ? "成功" : "失败"));

            //获取设备信息
            StfDevicesFields fields = responseData.getFields();

            if (responseData.isStatus()) {

                // 工作线程开始处理，这里用Thread.sleep()来模拟业务处理
                Thread.sleep(1000);

                StartAppiumServer server = new StartAppiumServer();
                server.start(fields);

                //执行UI自动化测试
                if (("Android").equals(fields.getPlatform())) {
                    AndroidUIAutomation androidUIAutomation = new AndroidUIAutomation();
                    responseData = androidUIAutomation.uiAutomation(fields, androidFile, phoneNum);

                } else if (("iOS").equals(fields.getPlatform())) {
                    IosUIAutomation iosUIAutomation = new IosUIAutomation();
                    responseData = iosUIAutomation.uiAutomation(fields, phoneNum);
                }

                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 执行UI自动化测试："
                        + (responseData.isStatus() ? "成功" : "失败"));

            }
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 线程" + Thread.currentThread().getName() + "执行完毕");
        } catch (Exception e) {
            e.printStackTrace();

            responseData = new ResponseData(false, e, "打印异常信息:" + e.getMessage());

        } finally {
            return responseData;
        }
    }
}
