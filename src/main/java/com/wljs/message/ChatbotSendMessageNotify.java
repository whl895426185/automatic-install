package com.wljs.message;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.TxtUtil;
import com.wljs.util.config.MessageConfig;
import com.wljs.util.config.SvnConfig;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 钉钉机器人消息通知项目群
 */
public class ChatbotSendMessageNotify {
    private Logger logger = LoggerFactory.getLogger(ChatbotSendMessageNotify.class);

    public void sendMessage(List<ResponseData> dataList) {
        try {
            //判断存放异常的文件夹是否存在
            String parentFilePath = MessageConfig.errorLogPath;
            File parentFile = new File(parentFilePath);
            if (!parentFile.exists() && !parentFile.isDirectory()) {
                parentFile.mkdir();
            }

            //创建这次需要发送异常信息的实时文件夹
            SimpleDateFormat fileFm = new SimpleDateFormat("YYYYMMddHHmmss");
            String childtime = fileFm.format(new Date());
            String childFilePath = parentFilePath + "/" + childtime;
            File childFile = new File(childFilePath);
            childFile.mkdir();

            //定时发送HTTP实例
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(MessageConfig.webHookToken);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");

            //定义钉钉消息的json
            String messagae = getDingDingMessage(dataList, childFilePath, childtime);

            StringEntity se = new StringEntity(messagae, "utf-8");
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发送钉钉消息异常：", e);
        }

    }

    //定义钉钉消息的json
    private String getDingDingMessage(List<ResponseData> dataList, String childFilePath, String childtime) throws IOException {
        //读取服务器存放当前部署安装的APK包信息
        TxtUtil txtUtil = new TxtUtil();
        String txtFilePath = SvnConfig.localApkVersionFilePath;
        String apkContent = txtUtil.readTxtFile(txtFilePath, SvnConfig.apkVersionLogFileName);

        SimpleDateFormat dfm = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        //钉钉消息，图片显示地址
        //定义钉钉消息的json
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("    \"actionCard\": {");
        buffer.append("    \"title\": \"STF监控异常报警\",");//这个必须包含“STF监控异常报警”因为自定义机器人的时候，填写了该关键字
        buffer.append("            \"text\": \"![screenshot](http://n.sinaimg.cn/transform/20141218/cesifvx7586610.jpg)\n\n" +
                "**部署APK包信息：**\n\n" +
                "(1) 包  名：" + apkContent.split("::")[0] + "\n\n" +
                "(2) 版  本：" + apkContent.split("::")[1] + "\n\n" +
                "(3) 上传时间：" + dfm.format(new Date(Long.valueOf(apkContent.split("::")[2]))) + "\n\n" +
                "(4) 上传备注：" + apkContent.split("::")[3] + "\",\n\n");
        buffer.append("            \"hideAvatar\": \"0\",");
        buffer.append("            \"btnOrientation\": \"0\",");
        buffer.append("            \"btns\": [");

        File file = new File(MessageConfig.htmlTemplatePath);
        InputStream inputStream = new FileInputStream(file);
        InputStreamReader read = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(read);
        String line = null;
        StringBuffer htmlBuffer = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            htmlBuffer.append(line + "\n");
        }
        bufferedReader.close();

        //把设备作为按钮形式展示，点击可以展示对应的日志
        String titleMsg = "";
        for (ResponseData responseData : dataList) {
            String logName = UUID.randomUUID().toString().replace("-", "");

            //创建实时日志，打开链接展示
            logger.info(":::::::::::::::::【钉钉消息通知】::::::::::::::::: logName = " + logName);
            //创建log文件，用于下载
            createLog(childFilePath, logName, responseData);
            //创建txt文件，用于展示
            createTxt(childFilePath, logName, responseData);

            //创建html，用于展示
            createHtml(htmlBuffer.toString(), childFilePath, childtime, logName, responseData, apkContent.split("::")[0]);

            StfDevicesFields fields = responseData.getFields();

            titleMsg += "    {";
            titleMsg += "        \"title\": \"" + fields.getDeviceName() + "\",";
            titleMsg += "        \"actionURL\": \"http://192.168.88.16/logs/appium/error/" + childtime + "/" + logName + ".html\"";
            titleMsg += "    },";
        }

        titleMsg = titleMsg.substring(0, titleMsg.length() - 1);

        buffer.append(titleMsg);
        buffer.append("]");
        buffer.append("},");
        buffer.append("    \"msgtype\": \"actionCard\"");
        buffer.append("}");

        return buffer.toString();
    }

    private String getExcMsg(String childFilePath, String logName, String apkName) {
        TxtUtil txtUtil = new TxtUtil();
        String message = txtUtil.readTxtFile(childFilePath, logName + ".txt");
        if (message.contains("Cannot start the '" + AndroidConfig.appPackage + "' application")) {
            return "请检查appActivity是否已经变动，导致无法启动APP，操作命令：adb -s 设备ID shell dumpsys activity|grep -i run";

        } else if (message.contains("The application at '/home/tools/android-package/wljs01/apk/" + apkName + "' does not exist or is not accessible")) {
            return "该路径下/home/tools/android-package/wljs01/apk/" + apkName + "的文件不存在或无法访问，请检查";
        } else {
            return null;
        }

    }

    private void createLog(String childFilePath, String logName, ResponseData responseData) throws IOException {
        createFile(childFilePath, logName + ".log", responseData);
    }

    private void createFile(String childFilePath, String logName, ResponseData responseData) throws IOException {
        String path = childFilePath + "/" + logName;
        logger.info(":::::::::::::::::【钉钉消息通知】::::::::::::::::: 创建文件:" + path);

        TxtUtil txtUtil = new TxtUtil();
        if (null == responseData.getException()) {
            txtUtil.creatTxtFile(childFilePath, logName);
            if (null != responseData.getAdbExceptionMsg()) {
                txtUtil.writeTxtFile(childFilePath, responseData.getAdbExceptionMsg(), logName);
            } else {
                txtUtil.writeTxtFile(childFilePath, responseData.getExMsg(), logName);
            }

        } else {

            File file = new File(path);
            //创建文件的输出流
            PrintStream stream = new PrintStream(file);
            responseData.getException().printStackTrace(stream);
            stream.flush();
            stream.close();
        }
    }

    private void createTxt(String childFilePath, String logName, ResponseData responseData) throws IOException {
        createFile(childFilePath, logName + ".txt", responseData);
    }

    private void createHtml(String htmlContent, String childFilePath, String childtime, String logName, ResponseData responseData, String apkName) {
        try {
            SimpleDateFormat fm = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

            Exception e = responseData.getException();
            StackTraceElement elements = (e == null ? null : e.getStackTrace()[0]);

            String path = "http://192.168.88.16/logs/appium/error/" + childtime + "/" + logName;
            htmlContent = htmlContent.replace("logHtmlPathValue", path + ".log");
            if (null == e && null != responseData.getAdbExceptionMsg()) {
                //ADB安裝包失敗，沒有直接抛出異常，所以手動維護異常
                htmlContent = htmlContent.replace("packageNameValue", "com.wljs.server.InstallApkServer");
                htmlContent = htmlContent.replace("fileNameValue", "InstallApkServer.java");
                htmlContent = htmlContent.replace("methodNameValue", "installApp");
                htmlContent = htmlContent.replace("lineNumValue", "");
            } else {
                htmlContent = htmlContent.replace("packageNameValue", null == elements ? "" : elements.getClassName());
                htmlContent = htmlContent.replace("fileNameValue", null == elements ? "" : elements.getFileName());
                htmlContent = htmlContent.replace("methodNameValue", null == elements ? "" : elements.getMethodName());
                htmlContent = htmlContent.replace("lineNumValue", null == elements ? "" : String.valueOf(elements.getLineNumber()));
            }
            htmlContent = htmlContent.replace("operateTime", fm.format(new Date()));

            //读取日志内容检查下具体的日志信息
            String newExMsg = getExcMsg(childFilePath, logName, apkName);
            ;

            if (null != newExMsg) {
                responseData.setExMsg(newExMsg);
            }

            htmlContent = htmlContent.replace("exceptionMsg", null == responseData.getExMsg() ? "" : responseData.getExMsg());
            htmlContent = htmlContent.replace("logPathValue", path + ".txt");
            if (null != responseData.getImagePath()) {//显示截图
                String imgPath = "http://192.168.88.16/logs/appium/error/images/" + responseData.getImagePath();
                htmlContent = htmlContent.replace("screenImgPath", imgPath);
                htmlContent = htmlContent.replace("<div id=\"screenImgDiv\" hidden=\"hidden\" style=\"margin-top: 50px;position: relative;margin-left: 30px;\">",
                        "<div id=\"screenImgDiv\" style=\"margin-top: 50px;position: relative;margin-left: 30px;\">");
            }

            String saveHtmlFile = childFilePath + "/" + logName + ".html";
            logger.info(":::::::::::::::::【钉钉消息通知】::::::::::::::::: 创建文件:" + saveHtmlFile);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveHtmlFile), "UTF-8"));
            bufferedWriter.write(htmlContent);
            bufferedWriter.newLine();// 换行

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        List<ResponseData> responseDataList = new ArrayList<ResponseData>();

        ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
        Exception exception = null;
        try {
            String a = null;
            a.toString();
        } catch (Exception e) {
            exception = e;

        }
        for (int i = 0; i < 3; i++) {
            ResponseData responseData = new ResponseData();

            StfDevicesFields fields = new StfDevicesFields();
            fields.setManufacturer("设备A");
            fields.setModel(String.valueOf(i));

            responseData.setFields(fields);
            responseData.setException(exception);

            responseData.setStatus(false);

            responseData.setExMsg("测试异常aaaaaa");
            responseDataList.add(responseData);
        }

        Exception exception2 = null;
        try {
            String[] a = {"1"};
            a[1].toString();
        } catch (Exception e) {
            exception = e;

        }

        ResponseData responseData = new ResponseData();

        StfDevicesFields fields = new StfDevicesFields();
        fields.setManufacturer("设备B");
        fields.setModel(String.valueOf(1));

        responseData.setFields(fields);
        responseData.setException(exception);

        responseData.setStatus(false);

        responseData.setExMsg("测试异常bbbbbbbb");
        responseDataList.add(responseData);

        messageNotify.sendMessage(responseDataList);
    }
}
