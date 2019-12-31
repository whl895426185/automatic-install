package com.wljs.install;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import com.wljs.dingding.DingdingMessage;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.testng.UiAutomation;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;


public class InstallAppPackage {
    private Logger logger = LoggerFactory.getLogger(InstallAppPackage.class);

    public static final String rethinkdb_host = "192.168.88.233";
    public static final int rethinkdb_port = 28015;

    /**
     * 连接rethinkdb数据库看看是否有连接且闲余的设备
     *
     * @return
     */
    private List<StfDevicesFields> getDeviceInformation() {
        RethinkDB r = RethinkDB.r;

        //开启rethinkdb连接
        Connection conn = r.connection().hostname(rethinkdb_host).port(rethinkdb_port).connect();

        //获取已连接且闲余的设备信息（present为true表示已连接，owner为空表示设备闲余）
        List<StfDevicesFields> dbList = null;
        Cursor<StfDevicesFields> cursor = r.db("stf")
                .table("devices")
                .filter(r.hashMap("present", true).with("owner", null).with("supportAutomation", 1))
                .filter(devices -> devices.g("platform").eq("Android").or(devices.g("platform").eq("iOS")))
                .withFields("manufacturer", "model", "serial", "version", "platform")
                .run(conn, Cursor.class);

        dbList = cursor.bufferedItems();

        //关闭Rethinkdb连接
        conn.close();


        if (null == dbList || dbList.size() < 1) {
            return null;
        }

        //获取安卓/IOS设备具体信息，初始化好端口信息
        List<StfDevicesFields> deviceList = getFilesList(dbList);

        return deviceList;

    }

    /**
     * 获取安卓或iOS设备信息
     *
     * @param dbList
     * @return
     */
    public List<StfDevicesFields> getFilesList(List<StfDevicesFields> dbList) {

        List<StfDevicesFields> fieldsList = new ArrayList<StfDevicesFields>();

        int appiumServerPort = 4723;//初始化appium端口
        int systemPort = 8200;//初始化system端口
        int wdaLocalPort = 8101;


        for (int i = 0; i < dbList.size(); i++) {
            JSONObject object = JSONObject.fromObject(dbList.get(i));
            StfDevicesFields fields = (StfDevicesFields) JSONObject.toBean(object, StfDevicesFields.class);
            fields.setAppiumServerPort(appiumServerPort);

            if (fields.getPlatform().equals("Android")) {
                fields.setSystemPort(systemPort);
                systemPort++;
            } else {
                fields.setWdaLocalPort(wdaLocalPort);
                wdaLocalPort++;
            }

            fields.setIndex(i);
            fieldsList.add(fields);
            appiumServerPort++;

        }
        return fieldsList;
    }


    /**
     * 准备执行安装
     *
     * @param androidFile
     * @param iosFile
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void executeInstall(String androidFile, String iosFile) throws InterruptedException, ExecutionException {
        if (null == androidFile && null == iosFile) {
            return;
        }
        //连接rethinkdb数据库看看是否有连接且闲余的设备
        List<StfDevicesFields> deviceList = getDeviceInformation();

        if (null == deviceList || deviceList.size() < 1) {
            return;
        }

        //利用Selenium调用浏览器，动态模拟浏览器事件，占用已连接且闲余的设备
        LoginStfPlatform loginStfPlatform = new LoginStfPlatform();
        List<StfDevicesFields> fieldsList = loginStfPlatform.occupancyResources(deviceList, androidFile, iosFile);


        //重新检查下设备是否是已连接且闲余到，避免中途设备被占用了
        if (null == fieldsList || fieldsList.size() < 1) {
            return;
        }

        //把设备信息放入java安全队列中，保证数据的安全性
        ArrayBlockingQueue queueList = new ArrayBlockingQueue(fieldsList.size());
        for (StfDevicesFields fields : fieldsList) {
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 开始执行自动部署安装工作！！！！！！");
            queueList.add(fields);
        }

        //创建一个线程池对象
        ExecutorService pool = Executors.newFixedThreadPool(queueList.size());
        List<Future<ResponseData>> futureList = new ArrayList<Future<ResponseData>>();

        for (int i = 1; i <= queueList.size(); i++) {
            ThreadConcurrent worker = new ThreadConcurrent(queueList, androidFile, iosFile);
            Future<ResponseData> future = pool.submit(worker);
            futureList.add(future);
        }

        List<StfDevicesFields> successList = new ArrayList<StfDevicesFields>();//部署成功的设备集合
        List<StfDevicesFields> failList = new ArrayList<StfDevicesFields>();//部署失败的设备集合
        List<ResponseData> resFailList = new ArrayList<ResponseData>();//部署失败的设备响应信息集合

        boolean androidDeviceFlag = false;
        boolean iosDeviceFlag = false;

        for (Future<ResponseData> future : futureList) {
            boolean doneFlag = false;
            while (!doneFlag) {//等待所有的设备安装完成
                if (future.isDone()) {
                    doneFlag = true;
                    ResponseData data = future.get();

                    StfDevicesFields result = data.getFields();

                    if (result == null) {
                        continue;
                    }
                    if (data.isStatus()) {
                        successList.add(result);
                    } else {

                        if (result.getPlatform().equals("Android")) {
                            androidDeviceFlag = true;
                        } else if (result.getPlatform().equals("iOS")) {
                            iosDeviceFlag = true;
                        }
                        failList.add(result);
                        resFailList.add(data);
                    }
                    logger.info(":::::::::::::::::【部署安装结果】::::::::::::::::: deviceName = " + (result == null ? "无" : result.getDeviceName()) + ", status = " + data.isStatus());

                }
            }
        }

        // 关闭线程池
        pool.shutdown();

        //统一释放的手机设备
        loginStfPlatform.releaseResources(deviceList);

        //失败的设备发送钉钉消息
        if (null != resFailList && resFailList.size() > 0) {
            logger.info(":::::::::::::::::检测到有部署安装失败的设备！！:::::::::::::::::");
            DingdingMessage messageNotify = new DingdingMessage();
            messageNotify.sendMessage(resFailList, androidDeviceFlag, iosDeviceFlag);
        }

        //成功的设备进行自动化测试
        UiAutomation automation = new UiAutomation();
        automation.createTestngXml(successList);
    }

}

