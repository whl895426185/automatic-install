package com.wljs.util;

import com.wljs.util.config.AndroidConfig;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 截圖
 */
public class ScreenshotUtil {
    /**
     * @param driver
     * @param uuid   设备ID
     * @throws IOException
     */
    public String screenshot(AndroidDriver driver, String uuid) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

        String dateStr = format.format(new Date());
        //生成图片的目录
        String dir_name = AndroidConfig.screenshotUrl + dateStr;
        //由于可能会存在图片的目录被删除的可能,所以我们先判断目录是否存在, 如果不在的话:
        if (!(new File(dir_name).isDirectory())) {
            //不存在的话就进行创建目录.
            new File(dir_name).mkdir();
        }
        //调用方法捕捉画面;
        File screen = driver.getScreenshotAs(OutputType.FILE);

        //复制文件到本地目录, 图片的最后存放地址为::
        String path = dir_name + "/" + uuid + ".jpg";
        FileUtils.copyFile(screen, new File(path));

        return dateStr + "/" + uuid + ".jpg";
    }

}
