package com.wljs.server;

import com.wljs.pojo.ResponseData;
import com.wljs.test.UIAutomationTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

public class OperateThreadServer implements Callable {
    private Logger logger = LoggerFactory.getLogger(OperateThreadServer.class);

    private ArrayBlockingQueue queue;
    private String apkPath;
    private int phoneNum;//始化手机号尾号，执行测试用例虚拟手机号用到

    public OperateThreadServer(ArrayBlockingQueue queue, String apkPath, int phoneNum) {
        this.queue = queue;
        this.apkPath = apkPath;
        this.phoneNum = phoneNum;
    }

    @Override
    public ResponseData call() throws Exception {
        ResponseData responseData = new ResponseData();
        try {

            logger.info("::::::::::::::::: 线程" + Thread.currentThread().getName() + "开始执行");
            InstallApkServer install = new InstallApkServer();
            responseData = install.init(queue, apkPath);

            if (responseData.isStatus()) {

                // 工作线程开始处理，这里用Thread.sleep()来模拟业务处理
                Thread.sleep(1000);

                UIAutomationTest uiTest = new UIAutomationTest();
                responseData = uiTest.executeTest(responseData.getFields(), apkPath, phoneNum);

            }
            logger.info(":::::::::::::::::【" + responseData.getFields().getDeviceName() + ">>>::::::::::::::::: 线程" + Thread.currentThread().getName() + "执行完毕");
        } catch (Exception e) {
            logger.error(":::::::::::::::::【" + responseData.getFields().getDeviceName() + ">>>::::::::::::::::: 自动部署安装失败:" + e);
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
            } finally {
                return responseData;
            }
        }
    }
}
