/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.authlib;

import com.mojang.authlib.ErrorCode;
import com.mojang.authlib.exceptions.AuthenticationException;
import java.io.File;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticationCpp {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Boolean LoadSuccess = AuthenticationCpp.LoadLibrary();

    private static Boolean SafeLoadLibrary(String path) {
        try {
            System.load(path);
            return true;
        }
        catch (UnsatisfiedLinkError e) {
            LOGGER.info(e.getMessage());
            return false;
        }
    }

    public static Boolean LoadLibrary() {
        try {
            String javaLibPath = System.getProperty("java.library.path");
            File runtime = new File(javaLibPath, "runtime");
            File[] files = runtime.listFiles();
            ArrayList<File> failedFiles = new ArrayList<File>();
            for (int i = 0; i < 10 && (i <= 0 || files.length > 0); ++i) {
                for (File file : files) {
                    if (!file.isFile() || !file.getName().contains("dll") || AuthenticationCpp.SafeLoadLibrary(file.getPath()).booleanValue()) continue;
                    failedFiles.add(file);
                }
                files = failedFiles.toArray(new File[failedFiles.size()]);
                failedFiles.clear();
            }
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage());
            return false;
        }
        return true;
    }

    public native int AuthenticationAccessToken(int var1, String var2);

    public Boolean Authentication(int port, String serverId) throws AuthenticationException {
        try {
            if (!LoadSuccess.booleanValue()) {
                throw new AuthenticationException("\u9354\u72ba\u6d47\u7481\u3088\u7609\u59af\u2033\u6f61\u6fb6\u8fab\u89e6");
            }
            int code = this.AuthenticationAccessToken(port, serverId);
            ErrorCode error = ErrorCode.GetErrorCode(code);
            LOGGER.info(error.getDescription());
            if (error != ErrorCode.SUCCESS) {
                throw new AuthenticationException(error.getDescription());
            }
            return Boolean.TRUE;
        }
        catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }
    }

    public static void main(String[] args) throws AuthenticationException {
        AuthenticationCpp test = new AuthenticationCpp();
        String port = System.getProperty("launcherControlPort");
        LOGGER.info(port);
        test.Authentication(Integer.parseInt(port), "");
    }
}

