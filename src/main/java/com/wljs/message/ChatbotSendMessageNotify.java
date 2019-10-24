package com.wljs.message;

import com.wljs.util.constant.ConfigConstant;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 钉钉机器人消息通知项目群
 */
public class ChatbotSendMessageNotify {

    public void sendMessage(String content) throws Exception {

        HttpClient httpclient = HttpClients.createDefault();

        HttpPost httppost = new HttpPost(ConfigConstant.webHookToken);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");

        String textMsg = "{\"msgtype\": \"text\", \"text\":{\"content\": \"" + content + "\"}}";

        StringEntity se = new StringEntity(textMsg, "utf-8");
        httppost.setEntity(se);

        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(result);
        }
    }

    public static void main(String args[]) throws Exception {
        ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
        messageNotify.sendMessage("我就是我, 是不一样的烟火");
    }
}
