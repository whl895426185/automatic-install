package com.wljs.message;

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

    public void sendMessage(String title, String content) {
        try {

            HttpClient httpclient = HttpClients.createDefault();

//            String token = "https://oapi.dingtalk.com/robot/send?access_token=102007644aec2459389393612d858f502ac4de4902cdad8482fdeb08c361b14e";
//            HttpPost httppost = new HttpPost(token);

            HttpPost httppost = new HttpPost(ConfigConstant.webHookToken);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");


            //读取APK包信息
            TxtUtil txtUtil = new TxtUtil();
            String apkText = txtUtil.readTxtFile(ConfigConstant.localApkVersionFilePath);

            StringBuffer buffer = new StringBuffer();
            buffer.append(" {");
            buffer.append("     \"msgtype\":\"actionCard\",");
            buffer.append("     \"actionCard\":{");
            buffer.append("         \"title\":\"STF监控异常报警\",");

            String text = "**STF监控异常报警**\n\n";

            if(null != apkText){
                text += "-------------------------------------\n\n";
                text += "**部署APK包信息：**\n\n";
                text += "包  名：" + apkText.split("::")[0] + "\n\n";
                text += "版  本：" + apkText.split("::")[1] + "\n\n";

                SimpleDateFormat fm = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

                long time = Long.valueOf(apkText.split("::")[2]);
                text += "上传时间：" + fm.format(new Date(time)) + "\n\n";
                text += "上传备注：" + apkText.split("::")[3] + "\n\n";
            }

            text += "-------------------------------------\n\n";
            text += "**" + title + "**\n\n";
            text += content;

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
        ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
        try {
            String a = null;
            a.toString();
        } catch (Exception e) {
            messageNotify.sendMessage("VIVO X9自动部署异常", e.toString());
        }
    }
}
