package com.wljs.server;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.TxtUtil;
import com.wljs.util.constant.ConfigConstant;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CyclicBarrier;

/**
 * 检测是否存在已连接且闲余的设备
 */
public class StfDevicesServer {
    private String uuid = UUID.randomUUID().toString().replaceAll("-", "");
    private Logger logger = LoggerFactory.getLogger(StfDevicesServer.class);

    public void getStfDevicesList(String apkPath) throws InterruptedException, IOException {
        RethinkDB r = RethinkDB.r;
        //连接rethinkdb
        Connection conn = r.connection().hostname(ConfigConstant.rethinkdb_host).port(ConfigConstant.rethinkdb_port).connect();

        //获取已连接且闲余的设备信息（present为true表示已连接，owner为空表示设备闲余）
        Cursor<StfDevicesFields> stfDevices = r.db(ConfigConstant.rethinkdb_dbName)
                .table(ConfigConstant.rethinkdb_tableName)
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
            logger.info(":::::::::::::::::【" + fields.getDeviceName() + "】开始执行自动部署安装工作！！！！！！");
            queueList.add(fields);
        }

        //创建uuid文件，用来查看是否是同一批设备
        File localFile = new File(ConfigConstant.uuidPath);
        if (!localFile.exists() && !localFile.isDirectory()) {
            localFile.mkdir();
        }
        TxtUtil txtUtil = new TxtUtil();
        txtUtil.creatTxtFile(ConfigConstant.uuidPath, uuid + ".txt");
        txtUtil.writeTxtFile(ConfigConstant.uuidPath, "::" + queueList.size(), uuid + ".txt");

        //初始化栅栏线程，用该线程控制并发
        CyclicBarrier cyclicBarrier = new CyclicBarrier(resultList.size());
        List<ResponseData> responseDataList = new ArrayList<ResponseData>();
        //phoneNum初始化手机号尾号，执行测试用例虚拟手机号用到
        for (int phoneNum = 1; phoneNum <= resultList.size(); phoneNum++) {
            ThreadWorkServer worker = new ThreadWorkServer(cyclicBarrier, queueList, apkPath, phoneNum, uuid, responseDataList);
            worker.start();
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

    public static void main(String[] arg) {
        StfDevicesFields fields = new StfDevicesFields();
        fields.setManufacturer("VIVO");
        fields.setModel(" X20A");
        fields.setSerial("cf83c8d0");
        fields.setVersion("8.1.0");
        fields.setAppiumServerPort(4723);
        fields.setSystemPort(8200);

        String deviceName = fields.getDeviceName();
        System.out.println(deviceName);
    }
}
