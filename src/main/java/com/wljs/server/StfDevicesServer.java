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

    public void getStfDevicesList(String apkPath) throws InterruptedException, ExecutionException {
        RethinkDB r = RethinkDB.r;
        //连接rethinkdb
        Connection conn = r.connection().hostname(RethinkdbConfig.rethinkdb_host).port(RethinkdbConfig.rethinkdb_port).connect();

        //获取已连接且闲余的设备信息（present为true表示已连接，owner为空表示设备闲余）
        Cursor<StfDevicesFields> stfDevices = r.db(RethinkdbConfig.rethinkdb_dbName)
                .table(RethinkdbConfig.rethinkdb_tableName)
                .filter(r.hashMap("present", true).with("owner", null))
                .withFields("manufacturer", "model", "serial", "version")
                .run(conn, Cursor.class);

        List<StfDevicesFields> dbList = stfDevices.bufferedItems();

        if (null == dbList || dbList.size() < 1) {
            return;
        }
        List<StfDevicesFields> fieldsList = new ArrayList<StfDevicesFields>();
        int appiumServerPort = 4723;
        int systemPort = 8200;
        for (int i = 0; i < dbList.size(); i++) {
            JSONObject object = JSONObject.fromObject(dbList.get(i));
            StfDevicesFields fields = (StfDevicesFields) JSONObject.toBean(object, StfDevicesFields.class);
            fields.setAppiumServerPort(appiumServerPort + i);
            fields.setSystemPort(systemPort + i);
            fieldsList.add(fields);
        }
        //关闭连接
        conn.close();

        //测试数据
        //List<StfDevicesFields> fieldsList = getFilesList();
        if (null == fieldsList || fieldsList.size() < 1) {
            return;
        }

        //利用Selenium调用浏览器，动态模拟浏览器事件，占用已连接且闲余的设备
        SeleniumServer seleniumServer = new SeleniumServer();
        List<StfDevicesFields> resultList = seleniumServer.occupancyResources(fieldsList);

        if (null == resultList || resultList.size() < 1) {
            return;
        }

        //把设备信息放入java安全队列中，保证数据的安全性
        ArrayBlockingQueue queueList = new ArrayBlockingQueue(resultList.size());
        for (StfDevicesFields fields : resultList) {
            logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: 开始执行自动部署安装工作！！！！！！");
            queueList.add(fields);
        }

        //创建一个线程池对象
        ExecutorService pool = Executors.newFixedThreadPool(queueList.size());
        List<Future<ResponseData>> futureList = new ArrayList<Future<ResponseData>>();

        //phoneNum初始化手机号尾号，执行测试用例虚拟手机号用到
        for (int phoneNum = 1; phoneNum <= queueList.size(); phoneNum++) {
            OperateThreadServer worker = new OperateThreadServer(queueList, apkPath, phoneNum);
            Future<ResponseData> future = pool.submit(worker);
            futureList.add(future);

        }

        List<ResponseData> resList = new ArrayList<ResponseData>();
        for (Future<ResponseData> future : futureList) {
            boolean doneFlag = false;
            while (!doneFlag){
                if(future.isDone()){
                    doneFlag = true;
                    ResponseData data = future.get();
                    logger.info(":::::::::::::::::【部署安装结果】::::::::::::::::: deviceName = " + (data.getFields() == null ? "无" : data.getFields().getDeviceName()) + ", status = " + data.isStatus() + ", ExMsg = " + data.getExMsg());

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
     * 自测数据
     *
     * @return
     */
    public List<StfDevicesFields> getFilesList() {
        int appiumServerPort = 4723;
        int systemPort = 8200;
        int index = 0;
        List<StfDevicesFields> fieldsList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StfDevicesFields fields = new StfDevicesFields();

            if (i == 0) {
                fields.setManufacturer("");
                fields.setModel(" M5s");
                fields.setSerial("612QKBQJ226WJ");
                fields.setVersion("6.0");
            }
            if (i == 1) {
//                fields.setManufacturer("OPPO");
//                fields.setModel(" R9s Plus");
//                fields.setSerial("39aafa2b");
//                fields.setVersion("6.0.1");

                fields.setManufacturer("VIVO");
                fields.setModel(" X20A");
                fields.setSerial("cf83c8d0");
                fields.setVersion("8.1.0");
            }
            if (i == 2) {
                fields.setSerial("8KE5T19711012159");
                fields.setVersion("9.0");
                fields.setModel(" P30");
                fields.setManufacturer("HUAWEI");
            }

            fields.setAppiumServerPort(appiumServerPort + index);
            fields.setSystemPort(systemPort + index);
            fieldsList.add(fields);
            index++;
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
