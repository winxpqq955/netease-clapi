/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 */
package com.netease.mc.mod.network.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.LogManager;

public class Common {
    public static boolean isDebugger = true;
    public static final String STRING_ENCODE = "ISO-8859-1";

    public static String getGameType(byte b) {
        switch (b) {
            case 0: {
                return "survival";
            }
            case 1: {
                return "creative";
            }
            case 2: {
                return "hardcore";
            }
        }
        return "survival";
    }

    public static void Log(String log) {
        if (isDebugger) {
            LogManager.getLogger().info(log);
        }
    }

    public static void CatchException(Throwable e) {
        StringWriter result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        LogManager.getLogger().info("Exception:" + ((Object)result).toString());
    }

    public static long getSystemTimeStamp() {
        return System.currentTimeMillis() / 1000L;
    }
}

