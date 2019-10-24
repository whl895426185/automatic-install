package com.wljs.message;

import com.sun.mail.util.MailSSLSocketFactory;
import com.wljs.util.constant.ConfigConstant;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 发送邮件通知
 */
public class SendEmailMessageNotify {
    /**
     * 邮件单发（自由编辑短信，并发送，适用于私信）
     *
     * @param emailContent 邮件内容
     * @throws Exception
     */
    public void sendEmail(String emailContent) {
        try {
            Properties props = new Properties();

            props.setProperty("mail.debug", "true"); // 开启debug调试
            props.setProperty("mail.smtp.auth", "true");// 发送服务器需要身份验证
            props.setProperty("mail.smtp.host", ConfigConstant.myEmailSMTPHost);// 设置邮件服务器主机名
            props.setProperty("mail.transport.protocol", "smtp");// 发送邮件协议名称

            props.put("mail.smtp.port", ConfigConstant.myEmailSMTPPort);// 端口号

            /**SSL认证，注意腾讯邮箱是基于SSL加密的，所以需要开启才可以使用**/
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);

            //设置是否使用ssl安全连接（一般都使用）
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", sf);

            //创建会话
            Session session = Session.getInstance(props);

            //添加需要发送附件或摘要
            Multipart multipart = new MimeMultipart();
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart = getAttachmentBodyPart(bodyPart);
            multipart.addBodyPart(bodyPart, 0);

            BodyPart bodyPart2 = new MimeBodyPart();
            bodyPart2.setText(getMailText(emailContent));
            multipart.addBodyPart(bodyPart2, 1);

            //发送邮件正文,发送的消息，基于观察者模式进行设计的
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(ConfigConstant.myEmailAccount, ConfigConstant.myEmailNickName, "UTF-8"));//设置发件人邮箱
            msg.setSubject(ConfigConstant.myEmailSubject);//设置邮件标题
            msg.setContent(multipart);//发送附件
            msg.setSentDate(new Date());//设置显示的发件时间
            msg.addHeader("charset", "UTF-8");

            //设定收件邮箱地址
            //RecipientType.TO   -- 收件人
            //RecipientType.CC   -- 抄送人
            //RecipientType.BCC  -- 暗送人[不显示发件人信息]

        /*String[] toEmailArradessArray = ConfigConstant.toEmailAddress.split(",");
        Address[] addressArray = new Address[toEmailArradessArray.length];
        for(int i=0 ;i<toEmailArradessArray.length;i++){
            addressArray[i] = new InternetAddress(toEmailArradessArray[i]);
        }*/

            InternetAddress toAddress = new InternetAddress(ConfigConstant.toEmailAddress);
            InternetAddress ccAddress = new InternetAddress(ConfigConstant.ccEmailAddress);
            msg.addRecipient(Message.RecipientType.TO, toAddress);
            msg.addRecipient(Message.RecipientType.CC, ccAddress);

            //得到邮差对象
            Transport transport = session.getTransport();

            //连接自己的邮箱账户，密码不是自己QQ邮箱的密码，而是在开启SMTP服务时所获取到的授权码
            transport.connect(ConfigConstant.myEmailSMTPHost, ConfigConstant.myEmailAccount, ConfigConstant.myEmailPassword);

            //发送邮件
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加邮件正文
     *
     * @param emailContent
     * @return
     */
    private String getMailText(String emailContent) {
        StringBuilder builder = new StringBuilder();

        //写入内容
        builder.append("\n" + emailContent);

        //定义要输出日期字符串的格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //在内容后加入邮件发送的时间
        builder.append("\n\n邮件发送时间：" + sdf.format(new Date()));

        return builder.toString();
    }

    /**
     * 添加附件
     *
     * @param bodyPart
     * @return
     */
    private BodyPart getAttachmentBodyPart(BodyPart bodyPart) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //添加附件
            File usFile = new File(ConfigConstant.errorLogPath + "error-log-" + format.format(new Date()) + ".log");
            DataSource source = new FileDataSource(usFile);
            bodyPart.setDataHandler(new DataHandler(source));
            bodyPart.setFileName(MimeUtility.encodeWord(usFile.getName()));
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bodyPart;
    }
}
