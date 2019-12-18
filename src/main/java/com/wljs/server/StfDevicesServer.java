package com.wljs.server;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import com.wljs.message.ChatbotSendMessageNotify;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.config.RethinkdbConfig;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 检测是否存在已连接且闲余的设备
 */
public class StfDevicesServer {
    private Logger logger = LoggerFactory.getLogger(StfDevicesServer.class);

    public void getStfDevicesList(String androidFile, String iosFile) throws InterruptedException, ExecutionException {
        if (null == androidFile && null == iosFile) {
            return;
        }

        RethinkDB r = RethinkDB.r;

        //开启rethinkdb连接
        Connection conn = r.connection().hostname(RethinkdbConfig.rethinkdb_host).port(RethinkdbConfig.rethinkdb_port).connect();

        //获取已连接且闲余的设备信息（present为true表示已连接，owner为空表示设备闲余）
        List<StfDevicesFields> dbList = null;
        Cursor<StfDevicesFields> cursor = r.db(RethinkdbConfig.rethinkdb_dbName)
                .table(RethinkdbConfig.rethinkdb_tableName)
                .filter(r.hashMap("present", true).with("owner", null).with("platform", "Android")).or(r.hashMap("platform", "iOS"))
                .withFields("manufacturer", "model", "serial", "version", "platform")
                .run(conn, Cursor.class);

        dbList = cursor.bufferedItems();

        //关闭Rethinkdb连接
        conn.close();

        if (null == dbList || dbList.size() < 1) {
            return;
        }

        //获取安卓/IOS设备具体信息，初始化好端口信息
        List<StfDevicesFields> deviceList = getFilesList(dbList);


        //利用Selenium调用浏览器，动态模拟浏览器事件，占用已连接且闲余的设备
        SeleniumServer seleniumServer = new SeleniumServer();
        List<StfDevicesFields> fieldsList = seleniumServer.occupancyResources(deviceList);


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

        //phoneNum初始化手机号尾号，执行测试用例虚拟手机号用到
        for (int phoneNum = 1; phoneNum <= queueList.size(); phoneNum++) {
            OperateThreadServer worker = new OperateThreadServer(queueList, androidFile, iosFile, phoneNum);
            Future<ResponseData> future = pool.submit(worker);
            futureList.add(future);

        }

        List<ResponseData> resList = new ArrayList<ResponseData>();
        boolean androidDeviceFlag = false; //true有安卓设备, false 没有
        boolean iosDeviceFlag = false;//true有iOS设备, false 没有
        for (Future<ResponseData> future : futureList) {
            boolean doneFlag = false;
            while (!doneFlag) {
                if (future.isDone()) {
                    doneFlag = true;
                    ResponseData data = future.get();
                    if (null != data.getFields()) {
                        if (data.getFields().getPlatform().equals("Android")) {
                            androidDeviceFlag = true;
                        }
                        if (data.getFields().getPlatform().equals("iOS")) {
                            iosDeviceFlag = true;
                        }

                    }

                    logger.info(":::::::::::::::::【部署安装结果】::::::::::::::::: deviceName = " + (data.getFields() == null ? "无" : data.getFields().getDeviceName()) + ", status = " + data.isStatus());

                    if (data.getFields() == null) {
                        continue;
                    }
                    if (data.isStatus()) {
                        continue;
                    }
                    resList.add(data);
                }
            }
        }

        // 关闭线程池
        pool.shutdown();

        //利用Selenium调用浏览器，动态模拟浏览器事件，释放设备资源
        seleniumServer.releaseResources(fieldsList);


        if (null != resList && resList.size() > 0) {
            logger.info(":::::::::::::::::检测到有部署安装失败的设备！！:::::::::::::::::");
            ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
            messageNotify.sendMessage(resList, androidDeviceFlag, iosDeviceFlag);
        }

        return;
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

        for (int i = 0; i < dbList.size(); i++) {
            JSONObject object = JSONObject.fromObject(dbList.get(i));
            StfDevicesFields fields = (StfDevicesFields) JSONObject.toBean(object, StfDevicesFields.class);
            fields.setAppiumServerPort(appiumServerPort);
            fields.setSystemPort(systemPort);

            fieldsList.add(fields);

            appiumServerPort++;
            systemPort++;
        }


        return fieldsList;
    }


}
