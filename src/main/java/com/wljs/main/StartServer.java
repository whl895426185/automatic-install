package com.wljs.main;

import com.wljs.message.ChatbotSendMessageNotify;
import com.wljs.pojo.ResponseData;
import com.wljs.server.StfDevicesServer;
import com.wljs.util.TxtUtil;
import com.wljs.util.constant.ConfigConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 启动类
 */
public class StartServer {
    private static Logger logger = LoggerFactory.getLogger(StartServer.class);

    public static void main(String[] args) throws SVNException {
        ResponseData responseData = new ResponseData();
        /**
         * 对版本库进行初始化操作 (在用版本库进行其他操作前必须进行初始化)
         * 对于通过使用 http:// 和 https:// 访问，执行DAVRepositoryFactory.setup();
         * 对于通过使用svn:// 和 svn+xxx://访问，执行SVNRepositoryFactoryImpl.setup();
         * 对于通过使用file:///访问，执行FSRepositoryFactory.setup();         *
         */
        SVNRepositoryFactoryImpl.setup();

        //初始化SVN登录账号密码
        String username = ConfigConstant.svnUserName;
        String password = ConfigConstant.svnPassword;

        //判断SVN检出文件目录是否存在
        File localFile = new File(ConfigConstant.localFilePath);
        if (!localFile.exists() && !localFile.isDirectory()) {
            localFile.mkdir();
        }

        //判断存放apkVersionLog.txt文件目录是否存在
        File localApkVersionFile = new File(ConfigConstant.localApkVersionFilePath);
        if (!localApkVersionFile.exists() && !localApkVersionFile.isDirectory()) {
            localApkVersionFile.mkdir();
        }

        //登录svn
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, username, password);

        SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);

        //把版本检出到该文件目录
        SVNURL repositoryURL = SVNURL.parseURIEncoded(ConfigConstant.svnFileUrl);

        //文件解锁
        svnClientManager.getWCClient().doCleanup(localFile);//前提必须有svn仓库目录
        //目录检出
        updateClient.doCheckout(repositoryURL, localFile, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);

        //获取当前日期（安卓同事说，不要日期文件夹区分，统一放一个文件夹下面）
        /*SimpleDateFormat format = new SimpleDateFormat("YYYYMMdd");
        Date date = new Date();
        String dateStr = format.format(date);*/

        //创建库连接
        SVNRepository repository = SVNRepositoryFactory.create(repositoryURL);
        //身份验证
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
        //创建身份验证管理器
        repository.setAuthenticationManager(authManager);

        //检测SVN当前日期的文件夹是否存在，不存在则新增（屏蔽）
        /*Collection fileEntries = repository.getDir(null, -1, null, (Collection) null);
        Iterator fileIterator = fileEntries.iterator();
        List<String> fileNameList = new ArrayList<String>();
        while (fileIterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) fileIterator.next();
            fileNameList.add(entry.getName());
        }
        if(!fileNameList.contains(dateStr)){
            //获得编辑器
            ISVNEditor editor = repository.getCommitEditor("add file", null,true,null);
            editor.openRoot(-1);
            editor.addDir(dateStr,null, -1);
            editor.closeDir();
            editor.closeEdit();

            svnClientManager.getCommitClient().doImport(localFile, repositoryURL,
                    "import operation!",null, false,false,SVNDepth.INFINITY);
        }*/

        //利用Timer的定时循环执行代码的功能
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
//                    SimpleDateFormat format2 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
//                    Date date2 = new Date();
//                    String dateStr2 = format2.format(date2);

//                    logger.info(dateStr2 + "当前准备执行更新svn仓库目录APK包信息： " + dateStr2);

                    //文件解锁
                    svnClientManager.getWCClient().doCleanup(localFile);

                    //定时更新SVN版本仓库文件
                    updateClient.doUpdate(localFile, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);

                    if (localFile.exists() && localFile.isDirectory()) {
                        SVNDirEntry entry = listEntries(repository/*, dateStr*/);
                        if (null != entry) {
                            String uploadFilePath = ConfigConstant.localFilePath + "/" + /*dateStr + "/" +*/ entry.getName();
                            //logger.info("检测到文件夹【" + uploadFilePath + "】有上传新的Android APK");

                            StfDevicesServer stfDevice = new StfDevicesServer();
                            stfDevice.getStfDevicesList(uploadFilePath);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("更新SVN版本仓库文件： 失败：" + e);

//                    TxtUtil texUtil = new TxtUtil();
//                    texUtil.deleteTxtFile(ConfigConstant.localApkVersionFilePath);

                    responseData.setStatus(false);
                    responseData.setException(e);
                    responseData.setExMsg("更新SVN版本仓库文件： 失败");

                } finally {
                    ChatbotSendMessageNotify messageNotify = new ChatbotSendMessageNotify();
                    List<ResponseData> responseDataList = new ArrayList<ResponseData>();
                    responseDataList.add(responseData);
                    messageNotify.sendMessage(responseDataList);
                }

            }
        }, 2000, 9000);
    }

    //检测是否上传了新的APK包
    public static SVNDirEntry listEntries(SVNRepository repository/*, String dateStr*/) throws IOException, SVNException {
        SVNDirEntry entry = null;
        //获取版本库的path目录下的所有条目。参数－1表示是最新版本。
        Collection entries = repository.getDir(null/*"/" + dateStr*/, -1, null, (Collection) null);
        Iterator iterator = entries.iterator();

        //本地先创建最新存放apk包的版本信息txt
        TxtUtil texUtil = new TxtUtil();
        texUtil.creatTxtFile(ConfigConstant.localApkVersionFilePath, ConfigConstant.apkVersionLogFileName);

        String name = "";// 存放最新上传包名
        Long time = 0l;// 存放最新上传包的时间

        //获取SVN当前当天最新的APK包
        Map<String, Object> objectMap = new HashMap<String, Object>();
        while (iterator.hasNext()) {
            entry = (SVNDirEntry) iterator.next();
            if (entry.getDate().getTime() > time) {
                if (!entry.getName().contains(".apk")) {
                    continue;
                }
                objectMap = new HashMap<String, Object>();
                objectMap.put(entry.getName(), entry);
                name = entry.getName();
                time = entry.getDate().getTime();
            } else {
                continue;
            }
        }

        if (objectMap.size() < 1) {
            return null;
        }

        //比对获取的最新APK包与本地记录的安装部署过的APK包的信息，是否已经安装部署过了

        //读取本地记录的安装部署过的APK包的信息
        String text = texUtil.readTxtFile(ConfigConstant.localApkVersionFilePath, ConfigConstant.apkVersionLogFileName);

        //比较
        entry = (SVNDirEntry) objectMap.get(name);

        String apkText = entry.getName();
        apkText += "::" + entry.getRevision();
        apkText += "::" + entry.getDate().getTime();
        if (null == entry.getCommitMessage()) {
            apkText += "::" + "无";
        } else {
            apkText += "::" + entry.getCommitMessage();
        }

        if (text.equals(apkText)) {
            //已经部署过了，忽略
            return null;
        }

        //先删除txt文档再新增
        if (!("").equals(text)) {
            texUtil.deleteTxtFile(ConfigConstant.localApkVersionFilePath, ConfigConstant.apkVersionLogFileName);
            texUtil.creatTxtFile(ConfigConstant.localApkVersionFilePath, ConfigConstant.apkVersionLogFileName);
        }
        //提前录入最新的APK包的信息，避免定時任務一直檢測
        texUtil.writeTxtFile(ConfigConstant.localApkVersionFilePath, apkText, ConfigConstant.apkVersionLogFileName);

        return entry;
    }

}
