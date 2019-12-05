package com.wljs.util;

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

    public String getProcess(String command) {
        StringBuffer buffer = new StringBuffer();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";

            while ((line = input.readLine()) != null) {
                buffer.append(line);
                logger.info(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
