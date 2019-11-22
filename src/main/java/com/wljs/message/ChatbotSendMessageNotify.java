package com.wljs.message;

import com.wljs.pojo.ResponseData;
import com.wljs.pojo.StfDevicesFields;
import com.wljs.util.TxtUtil;
import com.wljs.util.constant.ConfigConstant;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 钉钉机器人消息通知项目群
 */
public class ChatbotSendMessageNotify {

    public void sendMessage(List<ResponseData> dataList) {
        try {
            //判断是否有异常信息，有则发送钉钉，没有则返回
            if (null == dataList || dataList.size() < 1) {
                return;
            }

            //定义存放异常信息，key为异常信息，value为详细的异常信息及设备
            Map<String, List<ResponseData>> messageMap = new HashMap<String, List<ResponseData>>();
            List<ResponseData> responseDataList = null;
            for (ResponseData responseData : dataList) {
                //状态是成功的，则抛开掉
                if (responseData.isStatus()) {
                    continue;
                }

                //相同的异常，所涉及的设备信息放一块
                if (!messageMap.containsKey(responseData.getExMsg())) {
                    responseDataList = new ArrayList<ResponseData>();

                }
                responseDataList.add(responseData);

                messageMap.put(responseData.getExMsg(), responseDataList);
            }
            //判断是否为空
            if (null == messageMap || messageMap.size() < 1) {
                return;
            }

            //判断存放异常的文件夹是否存在
            String parentFilePath = ConfigConstant.errorLogPath;
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
            HttpPost httppost = new HttpPost(ConfigConstant.webHookToken);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");

            //定义钉钉消息的json
            String messagae = getDingDingMessage(messageMap, childFilePath, childtime);

            StringEntity se = new StringEntity(messagae, "utf-8");
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //定义钉钉消息的json
    private String getDingDingMessage(Map<String, List<ResponseData>> messageMap, String childFilePath, String childtime) throws IOException {
        //读取服务器存放当前部署安装的APK包信息
        TxtUtil txtUtil = new TxtUtil();
        String txtFilePath = ConfigConstant.localApkVersionFilePath;
        String apkContent = txtUtil.readTxtFile(txtFilePath, ConfigConstant.apkVersionLogFileName);

        SimpleDateFormat fm = new SimpleDateFormat("YYYYMMddHHmmss");

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
                "(3) 上传时间：" + fm.format(new Date(Long.valueOf(apkContent.split("::")[2]))) + "\n\n" +
                "(4) 上传备注：" + apkContent.split("::")[3] + "\",\n\n");
        buffer.append("            \"hideAvatar\": \"0\",");
        buffer.append("            \"btnOrientation\": \"0\",");
        buffer.append("            \"btns\": [");

        File file = new File("src/main/resources/template/ChatbotSendMsgTemplate.html");
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
        for (Map.Entry<String, List<ResponseData>> entry : messageMap.entrySet()) {
            List<ResponseData> reDataList = entry.getValue();

            buffer.append("    {");
            buffer.append("        \"title\": \"");
            int index = 0;
            String logName = fm.format(new Date());
            String deviceName = "";
            for (ResponseData responseData : reDataList) {
                //创建实时日志，打开链接展示
                if (index == 0) {

                    index++;
                    //创建log文件，用于下载
                    createLog(childFilePath, logName, responseData);

                    //创建html，用于展示
                    createHtml(htmlBuffer.toString(), childFilePath, logName, responseData);
                }

                StfDevicesFields fields = responseData.getFields();
                deviceName += fields.getDeviceName() + "##";
            }
            deviceName = deviceName.substring(0, deviceName.length()-2);

            buffer.append("        ##" + deviceName + "##");
            buffer.append("        \",");
            buffer.append("             \"actionURL\": \"http://192.168.88.16/logs/appium/error/" + childtime + "/" + logName +".html\"");
            buffer.append("    }");
        }

        buffer.append("]");
        buffer.append("},");
        buffer.append("    \"msgtype\": \"actionCard\"");
        buffer.append("}");

        return buffer.toString();
    }

    private void createLog(String childFilePath, String logName, ResponseData responseData) throws FileNotFoundException {
        File file = new File(childFilePath + "/" + logName + ".log");
        //创建文件的输出流
        PrintStream stream = new PrintStream(file);
        responseData.getException().printStackTrace(stream);
        stream.flush();
        stream.close();
    }

    private void createHtml(String htmlBuffer, String childFilePath, String logName, ResponseData responseData) {
        try {
            Exception e = responseData.getException();
            StackTraceElement elements = e.getStackTrace()[0];

            String htmlContent = htmlBuffer.toString();

            String path = "http://192.168.88.16/logs/appium/error/" + childFilePath + "/" + logName;
            htmlContent = htmlContent.replaceAll("logHtmlPathValue", path + ".log");
            htmlContent = htmlContent.replaceAll("packageNameValue", elements.getClassName());
            htmlContent = htmlContent.replaceAll("fileNameValue", elements.getFileName());
            htmlContent = htmlContent.replaceAll("methodNameValue", elements.getMethodName());
            htmlContent = htmlContent.replaceAll("lineNumValue", String.valueOf(elements.getLineNumber()));
            htmlContent = htmlContent.replaceAll("logPathValue", path + ".html");

            String saveHtmlFile = childFilePath + "/" + logName + ".html";
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveHtmlFile), "UTF-8"));
            bufferedWriter.write(htmlContent);
            bufferedWriter.newLine();// 换行
            /*
             * * 刷新该流的缓冲。 关键的一行代码。如果没有加这行代码。数据只是保存在缓冲区中。没有写进文件。 加了这行才能将数据写入目的地。
             * *
             */
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
