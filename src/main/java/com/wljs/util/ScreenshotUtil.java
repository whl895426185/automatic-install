package com.wljs.util;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 截圖
 */
public class ScreenshotUtil {

    //截屏存储文件目录
    public static final String screenshotUrl = "/usr/local/node/run/log/error/images/";

    public String screenshot(AndroidDriver androidDriver, IOSDriver iosDriver, String uuid) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

            String dateStr = format.format(new Date());
            //生成图片的目录
            String dir_name = screenshotUrl + dateStr;
            //由于可能会存在图片的目录被删除的可能,所以我们先判断目录是否存在, 如果不在的话:
            if (!(new File(dir_name).isDirectory())) {
                //不存在的话就进行创建目录.
                new File(dir_name).mkdir();
            }
            //调用方法捕捉画面;
            File screen = null;

            if (null != androidDriver) {
                screen = androidDriver.getScreenshotAs(OutputType.FILE);

            } else if (null != iosDriver) {
                screen = iosDriver.getScreenshotAs(OutputType.FILE);
            }


            //复制文件到本地目录, 图片的最后存放地址为::
            String path = dir_name + "/" + uuid + ".jpg";
            FileUtils.copyFile(screen, new File(path));

            return dateStr + "/" + uuid + ".jpg";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
