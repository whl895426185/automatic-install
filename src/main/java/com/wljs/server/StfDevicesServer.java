package com.wljs.server;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import com.wljs.message.ChatbotSendMessageNotify;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.config.RethinkdbConfig;
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
        RethinkDB r = RethinkDB.r;

        //开启rethinkdb连接
        Connection conn = r.connection().hostname(RethinkdbConfig.rethinkdb_host).port(RethinkdbConfig.rethinkdb_port).connect();

        //定义存放android设备集合
        List<StfDevicesFields> androidDbList = null;
        if(null != androidFile){

            //获取已连接且闲余的设备信息（present为true表示已连接，owner为空表示设备闲余）
            Cursor<StfDevicesFields> androidCursor = r.db(RethinkdbConfig.rethinkdb_dbName)
                    .table(RethinkdbConfig.rethinkdb_tableName)
                    .filter(r.hashMap("present", true).with("owner", null).with("platform", "Android"))
                    .withFields("manufacturer", "model", "serial", "version", "platForm")
                    .run(conn, Cursor.class);

            androidDbList = androidCursor.bufferedItems();

        }

        //定义存放ios设备集合
        List<StfDevicesFields> iosDbList = null;
        if(null != iosDbList){

            //获取已连接且闲余的设备信息（present为true表示已连接，owner为空表示设备闲余）
            Cursor<StfDevicesFields> iosCursor = r.db(RethinkdbConfig.rethinkdb_dbName)
                    .table(RethinkdbConfig.rethinkdb_tableName)
                    .filter(r.hashMap("present", true).with("owner", null).with("platform", "iOS"))
                    .withFields("manufacturer", "model", "serial", "version", "platForm")
                    .run(conn, Cursor.class);

            iosDbList = iosCursor.bufferedItems();

        }

        //关闭Rethinkdb连接
        conn.close();

        Boolean androidFlag = true;
        if (null == androidDbList || androidDbList.size() < 1) {
            logger.info(":::::::::::::::::没有检测到已连接且闲余到Android设备:::::::::::::::::");
            androidFlag = false;
        }

        Boolean iosFlag = true;
        if (null == iosDbList || iosDbList.size() < 1) {
            logger.info(":::::::::::::::::没有检测到已连接且闲余到iOS设备:::::::::::::::::");
            iosFlag = false;
        }


        if(!androidFlag && !iosFlag){
            return;
        }

        //获取安卓设备具体信息，初始化好端口信息
        List<StfDevicesFields> androidFieldsList = null;
        if(androidFlag){
            int appiumServerPort = 4723;//初始化appium端口
            int systemPort = 8200;//初始化system端口

            androidFieldsList = getFilesList(appiumServerPort, systemPort, androidDbList);

        }

        //获取iOS设备具体信息，初始化好端口信息
        List<StfDevicesFields> iosFieldsList = null;
        if(iosFlag){

            int appiumServerPort = 8723;//初始化appium端口
            int systemPort = 9200;//初始化system端口

            iosFieldsList = getFilesList(appiumServerPort, systemPort, iosDbList);

        }

        //利用Selenium调用浏览器，动态模拟浏览器事件，占用已连接且闲余的设备
        SeleniumServer seleniumServer = new SeleniumServer();
        List<StfDevicesFields> fieldsList = seleniumServer.occupancyResources(androidFieldsList, iosFieldsList);

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
        for (Future<ResponseData> future : futureList) {
            boolean doneFlag = false;
            while (!doneFlag) {
                if (future.isDone()) {
                    doneFlag = true;
                    ResponseData data = future.get();
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

        if (null != resList && resList.size() > 0) {
            logger.info(":::::::::::::::::检测到有部署安装失败的设备！！:::::::::::::::::");
            ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
            messageNotify.sendMessage(resList);
        }

        return;
    }

    /**
     * 获取安卓或iOS设备信息
     *
     * @return
     * @param appiumServerPort
     * @param systemPort
     * @param dbList
     */
    public List<StfDevicesFields> getFilesList(int appiumServerPort, int systemPort, List<StfDevicesFields> dbList) {

        List<StfDevicesFields> fieldsList = new ArrayList<StfDevicesFields>();
        for (int i = 0; i < dbList.size(); i++) {

            JSONObject object = JSONObject.fromObject(dbList.get(i));
            StfDevicesFields fields = (StfDevicesFields) JSONObject.toBean(object, StfDevicesFields.class);
            fields.setAppiumServerPort(appiumServerPort + i);
            fields.setSystemPort(systemPort + i);

            fieldsList.add(fields);
        }
        return fieldsList;
    }

   /* public static void main(String[] arg) throws ExecutionException, InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

        List<FutureTask<ResponseData>> taskList = new ArrayList<FutureTask<ResponseData>>();
        for (int phoneNum = 1; phoneNum <= 5; phoneNum++) {
            OperateThreadServer worker = new OperateThreadServer(cyclicBarrier, null, "", phoneNum);
            FutureTask<ResponseData> futureTask = new FutureTask<ResponseData>((Callable<ResponseData>) worker);
            new Thread(futureTask).start();

            taskList.add(futureTask);
        }

        List<ResponseData> resultResList = new ArrayList<ResponseData>();
        for(FutureTask<ResponseData> task : taskList){
            resultResList.add(task.get());
        }

        if(null != resultResList && resultResList.size() > 0){
            ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
            messageNotify.sendMessage(resultResList);
        }

    }*/

}
