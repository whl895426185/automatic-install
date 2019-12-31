package com.wljs.dingding;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.TxtUtil;
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
public class DingdingMessage {
    private Logger logger = LoggerFactory.getLogger(DingdingMessage.class);

    //token
    public static String webHookToken = "https://oapi.dingtalk.com/robot/send?access_token=102007644aec2459389393612d858f502ac4de4902cdad8482fdeb08c361b14e";
    //钉钉消息存放路径
    public static final String errorLogPath = "/usr/local/package/dingding/log/error";
    //消息HTML模板
    public static final String htmlTemplatePath = "/usr/local/package/dingding/template/ChatbotSendMsgTemplate.html";
    //错误日志访问URL
    private String url = "http://127.0.0.1:8080/logs/appium/error/";

    public void sendMessage(List<ResponseData> dataList, boolean androidDeviceFlag, boolean iosDeviceFlag) {
        try {
            //判断存放异常的文件夹是否存在
            String parentFilePath = errorLogPath;
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
            HttpPost httppost = new HttpPost(webHookToken);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");

            //定义钉钉消息的json
            String messagae = null;
            if (androidDeviceFlag || iosDeviceFlag) {
                messagae = getDevicesMessage(dataList, childFilePath, childtime, androidDeviceFlag, iosDeviceFlag);
            } else {
                messagae = getCommonMessage(dataList);
            }

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

    private String getCommonMessage(List<ResponseData> dataList) {
        StringBuffer buffer = new StringBuffer();

        ResponseData responseData = dataList.get(0);

        buffer.append("{");
        buffer.append("    \"msgtype\": \"text\",");
        buffer.append("        \"text\": {");
        buffer.append("   \"content\": \"" + responseData.getExMsg() + "\"");
        buffer.append("},");
        buffer.append("    \"at\": {");
        buffer.append("    \"atMobiles\": [");
        buffer.append("    \"13632504995\"");
        buffer.append("],");
        buffer.append("    \"isAtAll\": true");
        buffer.append("}");
        buffer.append("}");
        return buffer.toString();

    }

    //定义钉钉消息的json
    private String getDevicesMessage(List<ResponseData> dataList, String childFilePath, String childtime, boolean androidDeviceFlag, boolean iosDeviceFlag) throws IOException {
        //定义钉钉消息的json
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("    \"actionCard\": {");
        buffer.append("    \"title\": \"STF监控异常报警\",");//这个必须包含“STF监控异常报警”因为自定义机器人的时候，填写了该关键字

        //获取apk包信息
        String apkMsg = "";
        String apkName = "";
        if (androidDeviceFlag) {
            Map<String, String> apkMap = getApkMsg();

            apkMsg = apkMap.get("apkMsg");
            apkName = apkMap.get("apkName");
        }

        //获取ipa包信息
        String ipaMsg = "";
        String ipaName = "";
        if (iosDeviceFlag) {
            Map<String, String> apkMap = getIpaMsg();

            apkMsg = apkMap.get("ipaMsg");
            apkName = apkMap.get("ipaName");
        }

        buffer.append("            \"text\": \"![screenshot](http://n.sinaimg.cn/transform/20141218/cesifvx7586610.jpg)\n\n" + apkMsg + ipaMsg + ",");
        buffer.append("            \"hideAvatar\": \"0\",");
        buffer.append("            \"btnOrientation\": \"0\",");
        buffer.append("            \"btns\": [");

        File file = new File(htmlTemplatePath);
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
            createHtml(htmlBuffer.toString(), childFilePath, childtime, logName, responseData, apkName, ipaName);

            StfDevicesFields fields = responseData.getFields();

            titleMsg += "    {";
            titleMsg += "        \"title\": \"" + fields.getDeviceName() + "\",";
            titleMsg += "        \"actionURL\": \"" + url + childtime + "/" + logName + ".html\"";
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

    private Map<String, String> getIpaMsg() {
        Map<String, String> map = new HashMap<>();

        //读取服务器存放当前部署安装的APK包信息
        TxtUtil txtUtil = new TxtUtil();
        String txtFilePath = SvnConfig.svnIpaVersionTxtPath;
        String ipaContent = txtUtil.readTxtFile(txtFilePath, SvnConfig.ipaVersionLogFileName);

        SimpleDateFormat dfm = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        StringBuffer buffer = new StringBuffer();

        String ipaName = ipaContent.split("::")[0];
        String version = ipaContent.split("::")[1];
        String time = dfm.format(new Date(Long.valueOf(ipaContent.split("::")[2])));
        String remark = ipaContent.split("::")[3];

        buffer.append("**部署IPA包信息：**\n\n" +
                "(1) 包  名：" + ipaName + "\n\n" +
                "(2) 版  本：" + version + "\n\n" +
                "(3) 上传时间：" + time + "\n\n" +
                "(4) 上传备注：" + remark + "\"\n\n");

        map.put("ipaName", ipaName);
        map.put("ipaMsg", buffer.toString());
        return map;
    }

    private Map<String, String> getApkMsg() {
        Map<String, String> map = new HashMap<>();

        //读取服务器存放当前部署安装的APK包信息
        TxtUtil txtUtil = new TxtUtil();
        String txtFilePath = SvnConfig.svnApkVersionTxtPath;
        String apkContent = txtUtil.readTxtFile(txtFilePath, SvnConfig.apkVersionLogFileName);

        SimpleDateFormat dfm = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        StringBuffer buffer = new StringBuffer();

        String apkName = apkContent.split("::")[0];
        String version = apkContent.split("::")[1];
        String time = dfm.format(new Date(Long.valueOf(apkContent.split("::")[2])));
        String remark = apkContent.split("::")[3];

        buffer.append("**部署APK包信息：**\n\n" +
                "(1) 包  名：" + apkName + "\n\n" +
                "(2) 版  本：" + version + "\n\n" +
                "(3) 上传时间：" + time + "\n\n" +
                "(4) 上传备注：" + remark + "\"\n\n");

        map.put("apkName", apkName);
        map.put("apkMsg", buffer.toString());
        return map;

    }

    private String getExcMsg(String childFilePath, String logName, String apkName, String ipaName) {
        TxtUtil txtUtil = new TxtUtil();
        String message = txtUtil.readTxtFile(childFilePath, logName + ".txt");
        if (message.contains("Cannot start the com.sibu.futurebazaar application")) {
            return "请检查appActivity是否已经变动，导致无法启动APP，操作命令：adb -s 设备ID shell dumpsys activity|grep -i run";

        } else if (message.contains("The application at '/usr/local/package/wljs01/apk/" + apkName + "' does not exist or is not accessible")) {
            return "该路径下/usr/local/package/wljs01/apk/" + apkName + "的文件不存在或无法访问，请检查";
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

    private void createHtml(String htmlContent, String childFilePath, String childtime, String logName, ResponseData responseData, String apkName, String ipaName) {
        try {
            SimpleDateFormat fm = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

            Exception e = responseData.getException();
            StackTraceElement elements = (e == null ? null : e.getStackTrace()[0]);

            String path = url + childtime + "/" + logName;
            htmlContent = htmlContent.replace("logHtmlPathValue", path + ".log");

            htmlContent = htmlContent.replace("packageNameValue", null == elements ? "" : elements.getClassName());
            htmlContent = htmlContent.replace("fileNameValue", null == elements ? "" : elements.getFileName());
            htmlContent = htmlContent.replace("methodNameValue", null == elements ? "" : elements.getMethodName());
            htmlContent = htmlContent.replace("lineNumValue", null == elements ? "" : String.valueOf(elements.getLineNumber()));

            htmlContent = htmlContent.replace("operateTime", fm.format(new Date()));

            //读取日志内容检查下具体的日志信息
            String newExMsg = getExcMsg(childFilePath, logName, apkName, ipaName);


            if (null != newExMsg) {
                responseData.setExMsg(newExMsg);
            }

            htmlContent = htmlContent.replace("exceptionMsg", null == responseData.getExMsg() ? "" : responseData.getExMsg());
            htmlContent = htmlContent.replace("logPathValue", path + ".txt");
            if (null != responseData.getImagePath()) {//显示截图
                String imgPath = url + "images/" + responseData.getImagePath();
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

        DingdingMessage messageNotify = new DingdingMessage();
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

        messageNotify.sendMessage(responseDataList, false, false);
    }
}
