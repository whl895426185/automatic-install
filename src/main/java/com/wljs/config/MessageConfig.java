package com.wljs.config;

/**
 * 钉钉自定义机器人配置
 */
public class MessageConfig {

    //token
    public static String webHookToken = "https://oapi.dingtalk.com/robot/send?access_token=102007644aec2459389393612d858f502ac4de4902cdad8482fdeb08c361b14e";


    //钉钉消息存放路径
    public static final String errorLogPath = "/usr/local/package/dingding/log/error";

    //消息HTML模板
    public static final String htmlTemplatePath = "/usr/local/package/dingding/template/ChatbotSendMsgTemplate.html";


}
