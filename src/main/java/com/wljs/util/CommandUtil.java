package com.wljs.util;

import com.wljs.pojo.StfDevicesFields;
import org.dom4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

/**
 * 执行命令，返回结果
 */
public class CommandUtil {
    private Logger logger = LoggerFactory.getLogger(CommandUtil.class);

    public String getProcess(String command, StfDevicesFields fields) {
        StringBuffer buffer = new StringBuffer();
        try {
            Process process = Runtime.getRuntime().exec(command);

            return getProcessStr(process, fields);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getProcessStr(Process process, StfDevicesFields fields) {

        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";

            while ((line = input.readLine()) != null) {
                buffer.append(line);
//                logger.info(":::::::::::::::::<<<" + fields.getDeviceName() + ">>>::::::::::::::::: " + line);
            }
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();


    }


    public void stopProcess(StfDevicesFields fields) {
        try {
            String command = "lsof -i:" + fields.getWdaLocalPort();
            Process process = Runtime.getRuntime().exec(command);

            String processStr = getProcessStr(process, fields);

            if (processStr != null && !("").equals(processStr)) {
                String pid = processStr.split("panxi")[0];
                pid = pid.split("node")[1];

                if (pid != null) {
                    getProcess("kill -9 " + pid, fields);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] arg) {
        try {
            String command = "lsof -i:10000";
            Process process = Runtime.getRuntime().exec(command);


            Class<?> clazz = Class.forName("java.lang.UNIXProcess");
            Field field = clazz.getDeclaredField("pid");
            field.setAccessible(true);
            int pid = (Integer) field.get(process);

            System.out.println(pid);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}
