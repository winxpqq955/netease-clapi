/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package com.netease.mc.mod.network.common;

import com.google.gson.Gson;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.UserPropertiesEx;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GameState {
    public static short gameid = 0;
    public static int uid = 0;
    public static int launcherport = 0;
    public static String launchversion = "";
    public static int timedelta = 0;
    public static ArrayList<Integer> acceptList = new ArrayList();
    public static UserPropertiesEx userPropertiesEx = new UserPropertiesEx();
    public static boolean isInit = false;
    public static ScheduledExecutorService schduler = Executors.newScheduledThreadPool(10);
    public static GameS gameState;

    public static void initGameType(String[] args) {
        try {
            for (int i = 0; i < args.length; ++i) {
                if (!args[i].equals("--userPropertiesEx")) continue;
                Gson gson = new Gson();
                Common.Log("initGameType0:" + args[i + 1]);
                userPropertiesEx = (UserPropertiesEx)gson.fromJson(args[i + 1], UserPropertiesEx.class);
                Common.Log("initGameType:" + GameState.userPropertiesEx.channel);
                break;
            }
        }
        catch (Throwable e) {
            Common.CatchException(e);
        }
    }

    public static boolean needCheck() {
        return (GameState.userPropertiesEx.GameType == 0 || GameState.userPropertiesEx.GameType == 2 || GameState.userPropertiesEx.GameType == 8) && !GameState.userPropertiesEx.isFilter;
    }

    public static String getChannel() {
        Common.Log("getChannel:" + GameState.userPropertiesEx.channel);
        return GameState.userPropertiesEx.channel;
    }

    public static enum GameS {
        INIT,
        LOAD,
        SINGLE,
        SERVER,
        CLIENT;

    }
}

