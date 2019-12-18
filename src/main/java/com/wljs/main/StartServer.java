package com.wljs.main;

import com.wljs.message.ChatbotSendMessageNotify;
import com.wljs.pojo.ResponseData;
import com.wljs.server.StfDevicesServer;
import com.wljs.util.TxtUtil;
import com.wljs.config.SvnConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 启动类，定时监控svn是否上传了新的apk包和ipa包，如有上传则自动部署安装到设备上
 * <p>
 * svn登陆方式：
 * ####对于通过使用 http:// 和 https:// 访问，执行DAVRepositoryFactory.setup();
 * ####对于通过使用svn:// 和 svn+xxx://访问，执行SVNRepositoryFactoryImpl.setup();
 * ####对于通过使用file:///访问，执行FSRepositoryFactory.setup();
 */
public class StartServer {
    private static Logger logger = LoggerFactory.getLogger(StartServer.class);

    public static void main(String[] args) throws SVNException{
        //初始化svn访问方式
        SVNRepositoryFactoryImpl.setup();

        //初始化SVN登录账号密码
        String username = SvnConfig.svnLoginName;
        String password = SvnConfig.svnLoginPwd;

        //登录svn
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance(options, username, password);

        //获取svn仓库url
        SVNURL repositoryURL = SVNURL.parseURIEncoded(SvnConfig.svnUrl);

        //创建库连接
        SVNRepository repository = SVNRepositoryFactory.create(repositoryURL);
        //身份验证
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
        //创建身份验证管理器
        repository.setAuthenticationManager(authManager);


        //文件解锁(这一步为了防止锁库)
        File file = getFile();
        svnClientManager.getWCClient().doCleanup(file);//前提必须有svn仓库目录


        //checkOut apk/ipa包
        SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
//        updateClient.doCheckout(repositoryURL, file, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);

        //利用Timer的定时循环执行代码的功能
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {

                    //文件解锁
                    svnClientManager.getWCClient().doCleanup(file);

                    //定时更新SVN仓库apk文件
                    updateClient.doUpdate(file, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);

                    if (file.exists() && file.isDirectory()) {

                        //检测是否有新上传到apk文件
                        String androidFile = getUploadFile(repository, SvnConfig.svnApkVersionTxtPath, "apk", SvnConfig.apkVersionLogFileName);

                        String iosFile = getUploadFile(repository, SvnConfig.svnIpaVersionTxtPath, "ipa", SvnConfig.ipaVersionLogFileName);


                        StfDevicesServer stfDevice = new StfDevicesServer();
                        stfDevice.getStfDevicesList(androidFile, iosFile);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("更新SVN版本仓库文件： 失败：" + e);

                    ResponseData responseData = new ResponseData(false, e, "更新SVN版本仓库文件： 失败");

                    List<ResponseData> responseDataList = new ArrayList<ResponseData>();
                    responseDataList.add(responseData);

                    ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
                    messageNotify.sendMessage(responseDataList, false, false);

                }

            }

        }, 2000, 50000);
    }

    /**
     * 检测是否有新上传到apk/ipa文件
     *
     * @param repository
     * @param versionTxtPath
     * @param type
     * @param versionLogFileName
     * @return
     */
    public static String getUploadFile(SVNRepository repository, String versionTxtPath, String type, String versionLogFileName) throws SVNException, IOException {
        //获取版本库的path目录下的所有条目。参数－1表示是最新版本。
        Collection entries = repository.getDir("/" + type, -1, null, (Collection) null);
        Iterator iterator = entries.iterator();

        //循环匹配，获取最新上传到文件信息
        SVNDirEntry entry = null;

        //上传文件包名
        String uploadFileName = "";

        //上传文件时间
        Long uploadTime = 0l;// 存放最新上传包的时间

        Map<String, Object> objectMap = new HashMap<String, Object>();
        while (iterator.hasNext()) {
            entry = (SVNDirEntry) iterator.next();
            if (entry.getDate().getTime() > uploadTime) {
                if (!entry.getName().contains("." + type)) {
                    continue;
                }
                objectMap = new HashMap<String, Object>();
                objectMap.put(entry.getName(), entry);
                uploadFileName = entry.getName();
                uploadTime = entry.getDate().getTime();
            } else {
                continue;
            }
        }

        if (objectMap.size() < 1) {
            return null;
        }

        //获取最新上传文件到信息
        entry = (SVNDirEntry) objectMap.get(uploadFileName);

        StringBuffer uploadFileBuffer = new StringBuffer();

        uploadFileBuffer.append(entry.getName());
        uploadFileBuffer.append("::" + entry.getRevision());
        uploadFileBuffer.append("::" + entry.getDate().getTime());
        uploadFileBuffer.append("::" + (null == entry.getCommitMessage() ? "无" : entry.getCommitMessage()));


        TxtUtil txtUtil = new TxtUtil();

        //创建apkVersionLog.txt文件
        txtUtil.creatTxtFile(versionTxtPath, versionLogFileName);

        //读取apkVersionLog.txt文件内容
        String text = txtUtil.readTxtFile(versionTxtPath, versionLogFileName);


        //匹配txt记录到文件信息是否是最新上传到文件信息，内容相等，则不用执行自动部署，没有则执行自动部署工作
        if (text.equals(uploadFileBuffer.toString())) {
            return null;
        }

        //先删除txt文档再新增
        if (!("").equals(text)) {
            txtUtil.deleteTxtFile(versionTxtPath, versionLogFileName);
            txtUtil.creatTxtFile(versionTxtPath, versionLogFileName);
        }

        //提前录入上传文件的信息，避免定时任务一直在检测
        txtUtil.writeTxtFile(versionTxtPath, uploadFileBuffer.toString(), versionLogFileName);


        String uploadFilePath = SvnConfig.filePath + "/" + type + "/" + entry.getName();
        logger.info("检测到文件夹【" + uploadFilePath + "】有上传新的" + type + "文件");

        return uploadFilePath;
    }


    /**
     * 检出文件之前先判断检出存放apk/ipa文件目录是否存在，不存在则创建
     */
    private static File getFile() {
        //判断apk包检出目录是否存在，不存在则创建
        File File = new File(SvnConfig.filePath);
        if (!File.exists() && !File.isDirectory()) {
            File.mkdir();
        }

        //判断存放apkVersionLog.txt文件目录是否存在，不存在则创建
        File svnApkVersionTxtPathFile = new File(SvnConfig.svnApkVersionTxtPath);
        if (!svnApkVersionTxtPathFile.exists() && !svnApkVersionTxtPathFile.isDirectory()) {
            svnApkVersionTxtPathFile.mkdir();
        }

        //判断存放ipaVersionLog.txt文件目录是否存在，不存在则创建
        File svnIpaVersionTxtPathFile = new File(SvnConfig.svnIpaVersionTxtPath);
        if (!svnIpaVersionTxtPathFile.exists() && !svnIpaVersionTxtPathFile.isDirectory()) {
            svnIpaVersionTxtPathFile.mkdir();
        }

        return File;
    }


}
