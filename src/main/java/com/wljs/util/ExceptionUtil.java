package com.wljs.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionUtil {

    /**
     * 返回完整的异常信息
     *
     * @param e
     * @return
     */
    public String exceptionMsg(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos));
        String exception = baos.toString();
        return exception;
    }

    /**
     * 返回adb命令执行的异常信息
     *
     * @param process
     * @return
     */
    public String adbExceptionMsg(String process) {
        if (process == null) {
            return null;
        }
        if (process.contains("INSTALL_FAILED_UID_CHANGED")) {
            return "卸载不完全，有残留文件，导致无法安装";

        } else if (process.contains("INSTALL_FAILED_INSUFFICIENT_STORAGE")) {
            return "没有足够的存储空间";

        } else if (process.contains("INSTALL_FAILED_INVALID_APK")) {
            return "无效的apk";

        } else if (process.contains("INSTALL_FAILED_OLDER_SDK")) {
            return "系统版本过旧";

        } else if (process.contains("INSTALL_FAILED_NEWER_SDK")) {
            return "系统版本过新";

        } else if (process.contains("INSTALL_FAILED_CPU_ABI_INCOMPATIBLE")) {
            return "包含的本机代码不兼容";

        } else if (process.contains("CPU_ABIINSTALL_FAILED_MISSING_FEATURE")) {
            return "使用了一个无效的特性";

        } else if (process.contains("INSTALL_FAILED_MEDIA_UNAVAILABLE")) {
            return "SD卡不存在";

        } else if (process.contains("INSTALL_FAILED_CONTAINER_ERROR")) {
            return "SD卡访问失败";

        } else if (process.contains("INSTALL_FAILED_INVALID_INSTALL_LOCATION")) {
            return "无效的安装路径";

        } else if (process.contains("INSTALL_FAILED_INTERNAL_ERROR")) {
            return "系统问题导致安装失败";

        } else if (process.contains("INSTALL_PARSE_FAILED_MANIFEST_MALFORMED")) {
            return "解析软件包时出现问题";

        } else if (process.contains("INSTALL_FAILED_USER_RESTRICTED")) {
            return "需要在手机中设置允许通过USB安装应用";

        } else if (process.contains("INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES")) {
            return "此apk已经安装过，且已经安装的apk和待安装的apk签名不一致";

        } else if (process.contains("INSTALL_FAILED_ALREADY_EXISTS")) {
            return "该应用已存在";

        } else if (process.contains("INSTALL_FAILED_DUPLICATE_PACKAGE")) {
            return "已存在同名程序";

        } else if (process.contains("INSTALL_FAILED_UPDATE_INCOMPATIBLE")) {
            return "版本不能共存";

        } else if (process.contains("INSTALL_FAILED_NO_SHARED_USER")) {
            return "要求的共享用户不存在";

        } else if (process.contains("INSTALL_FAILED_INVALID_URI")) {
            return "无效的链接";

        } else if (process.contains("INSTALL_FAILED_SHARED_USER_INCOMPATIBLE")) {
            return "需求的共享用户签名错误";

        } else if (process.contains("INSTALL_FAILED_MISSING_SHARED_LIBRARY")) {
            return "需求的共享库已丢失";

        } else if (process.contains("INSTALL_FAILED_REPLACE_COULDNT_DELETE")) {
            return "需求的共享库无效";

        } else if (process.contains("INSTALL_FAILED_DEXOPT")) {
            return "dex优化验证失败";

        } else if (process.contains("INSTALL_FAILED_CONFLICTING_PROVIDER")) {
            return "存在同名的内容提供者";

        } else if (process.contains("INSTALL_FAILED_TEST_ONLY")) {
            return "调用者不被允许测试的测试程序";

        } else if (process.contains("Failure")) {
            return "Failure";
        } else {
            return null;
        }
    }


}
