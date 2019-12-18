package com.wljs.util;

import com.wljs.pojo.StfDevicesFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
}
