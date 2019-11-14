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
import org.apache.http.util.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 钉钉机器人消息通知项目群
 */
public class ChatbotSendMessageNotify {

    public void sendMessage(ResponseData responseData) {
        try {
            if (responseData.isStatus()) {
                return;
            }

            StfDevicesFields fields = responseData.getFields();
            Exception exception = responseData.getException();

            String message = "";

            StackTraceElement stackTraceElement = exception.getStackTrace()[0];
            message += "包类名： " + stackTraceElement.getClassName() + "\n\n";
            message += "文件名： " + stackTraceElement.getFileName() + "\n\n";
            message += "方法名： " + stackTraceElement.getMethodName() + "\n\n";
            message += "报错行号： " + stackTraceElement.getLineNumber() + "\n\n";
            message += "异常信息： " + responseData.getExMsg() + "\n\n";

            HttpClient httpclient = HttpClients.createDefault();

//            String token = "https://oapi.dingtalk.com/robot/send?access_token=102007644aec2459389393612d858f502ac4de4902cdad8482fdeb08c361b14e";
//            HttpPost httppost = new HttpPost(token);

            HttpPost httppost = new HttpPost(ConfigConstant.webHookToken);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");


            //读取APK包信息
            TxtUtil txtUtil = new TxtUtil();
//            String apkText = txtUtil.readTxtFile("D:\\apkPackage");
            String apkText = txtUtil.readTxtFile(ConfigConstant.localApkVersionFilePath);

            StringBuffer buffer = new StringBuffer();
            buffer.append(" {");
            buffer.append("     \"msgtype\":\"actionCard\",");
            buffer.append("     \"actionCard\":{");
            buffer.append("         \"title\":\"STF监控异常报警\",");
            String text = "";
            if (null == fields) {
                text = "**报警！！！！**\n\n";
            } else {
                text = "**报警！！！！监控设备（" + fields.getDeviceName() + "）**\n\n";
            }

            SimpleDateFormat fm = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            if (null != apkText) {
                text += "-------------------------------------\n\n";
                text += "**部署APK包信息：**\n\n";
                text += "包  名：" + apkText.split("::")[0] + "\n\n";
                text += "版  本：" + apkText.split("::")[1] + "\n\n";


                long time = Long.valueOf(apkText.split("::")[2]);
                text += "上传时间：" + fm.format(new Date(time)) + "\n\n";
                text += "上传备注：" + apkText.split("::")[3] + "\n\n";
            }

            text += "-------------------------------------\n\n";
            text += "**JAVA异常日志信息打印（" + fm.format(new Date()) + "）：**\n\n";
            text += message;

            buffer.append("         \"text\":\"" + text + "\"");
            buffer.append("            }");
            buffer.append("     }");


            StringEntity se = new StringEntity(buffer.toString(), "utf-8");
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

    public static void main(String args[]) {
        ResponseData responseData = new ResponseData();

        try {
            String a = null;
            a.toString();
        } catch (Exception e) {
            responseData.setStatus(false);
            responseData.setException(e);
            responseData.setExMsg("测试异常");
        } finally {
            ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
            messageNotify.sendMessage(responseData);
        }
    }
}
