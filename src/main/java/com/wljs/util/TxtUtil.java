package com.wljs.util;

import java.io.*;

public class TxtUtil {
    /**
     * 创建文件
     *
     * @throws IOException
     */
    public void creatTxtFile(String path, String fileName) throws IOException {
        File filename = new File(path + "/" + fileName);

        if (!filename.exists()) {
            filename.createNewFile();
        }

    }

    /**
     * 刪除文件
     *
     * @throws IOException
     */
    public void deleteTxtFile(String path, String fileName) {
        File filename = new File(path + "/" + fileName);

        if (filename.exists()) {
            filename.delete();
        }
    }

    /**
     * 写文件
     *
     * @param newStr 新内容
     * @throws IOException
     */
    public boolean writeTxtFile(String path, String newStr, String fileName) throws IOException {
        // 先读取原有文件内容，然后进行写入操作
        boolean flag = false;
        String filein = newStr + "\r\n";
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            // 文件路径
            File file = new File(path + "/" + fileName);
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            // 保存该文件原有的内容
            for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf = buf.append(temp);
                // System.getProperty("line.separator")
                // 行与行之间的分隔符 相当于“\n”
                buf = buf.append(System.getProperty("line.separator"));
            }
            buf.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {
            // TODO 自动生成 catch 块
            throw e1;
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return flag;
    }


    /**
     * 读取内容
     *
     * @param path
     */
    public String readTxtFile(String path, String fileName) {
        try {
            File file = new File(path + "/" + fileName);
            if (file.isFile() && file.exists()) {
                StringBuffer sbf = new StringBuffer();
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    sbf.append(lineTxt);
                }
                br.close();
                return sbf.toString();
            } else {
                System.out.println("文件不存在!");
            }
        } catch (Exception e) {
            System.out.println("文件读取错误!");
        }

        return null;
    }

}
