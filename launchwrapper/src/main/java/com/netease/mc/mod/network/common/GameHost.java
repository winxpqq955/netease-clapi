/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 */
package com.netease.mc.mod.network.common;

import com.google.gson.Gson;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.common.Library;
import com.netease.mc.mod.network.common.UserPropertiesEx;
import java.util.concurrent.TimeUnit;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.launchwrapper.network.protocol.LauncherPort;
import net.minecraft.launchwrapper.network.protocol.LauncherPortArray;

public class GameHost {
    private static final String GAMEID = "gameid";
    private static final String UID = "uid";
    private static final String LAUNCHER_PORT = "launcherport";

    public static void init(String[] args) {
        try {
            LauncherPort entity;
            int idx;
            if (args == null) {
                Common.Log("arg is null");
                return;
            }
            Gson gson = new Gson();
            Common.Log(gson.toJson((Object)args));
            OptionParser parser = new OptionParser();
            parser.allowsUnrecognizedOptions();
            ArgumentAcceptingOptionSpec userPropertiesOption = parser.accepts("userProperties", "").withRequiredArg();
            ArgumentAcceptingOptionSpec userPropertiesExOption = parser.accepts("userPropertiesEx", "").withOptionalArg();
            OptionSet options = parser.parse(args);
            String json = (String)options.valueOf((OptionSpec)userPropertiesOption);
            String jsonEx = (String)options.valueOf((OptionSpec)userPropertiesExOption);
            Common.Log(json);
            Common.Log(jsonEx);
            if (jsonEx != null && !jsonEx.isEmpty()) {
                GameState.userPropertiesEx = (UserPropertiesEx)gson.fromJson(jsonEx, UserPropertiesEx.class);
                if (GameState.needCheck()) {
                    GameState.schduler.schedule(new Runnable(){

                        @Override
                        public void run() {
                            Library.Init();
                        }
                    }, 1L, TimeUnit.MILLISECONDS);
                }
            }
            LauncherPortArray array = (LauncherPortArray)gson.fromJson(json, LauncherPortArray.class);
            if (array.launcherport != null) {
                for (idx = 0; idx < array.launcherport.length; ++idx) {
                    if (array.launcherport[idx] == 0) continue;
                    GameState.launcherport = array.launcherport[idx];
                    break;
                }
            }
            if (GameState.launcherport == 0 && (entity = (LauncherPort)gson.fromJson(json, LauncherPort.class)) != null && entity.launcherport != 0) {
                GameState.launcherport = entity.launcherport;
            }
            if (array.uid != null) {
                for (idx = 0; idx < array.uid.length; ++idx) {
                    if (array.uid[idx] == 0) continue;
                    GameState.uid = array.uid[idx];
                    break;
                }
            }
            if (GameState.uid == 0 && (entity = (LauncherPort)gson.fromJson(json, LauncherPort.class)) != null) {
                if (LauncherPort.uid != 0) {
                    GameState.uid = LauncherPort.uid;
                }
            }
            if (array.timedelta != null) {
                for (int idx2 = 0; idx2 < array.timedelta.length; ++idx2) {
                    if (array.timedelta[idx2] == 0) continue;
                    GameState.timedelta = array.timedelta[idx2];
                    break;
                }
            } else {
                entity = (LauncherPort)gson.fromJson(json, LauncherPort.class);
                if (entity != null && entity.timedelta != 0) {
                    GameState.uid = entity.timedelta;
                }
            }
            if (json.contains("launchversion")) {
                if (array.launchversion != null && array.launchversion.length > 0) {
                    GameState.launchversion = array.launchversion[0];
                } else {
                    entity = (LauncherPort)gson.fromJson(json, LauncherPort.class);
                    if (entity != null) {
                        GameState.launchversion = entity.launchversion;
                    }
                }
                Common.Log("GameState.launchVersion:" + GameState.launchversion);
            }
            GameState.isInit = true;
        }
        catch (Exception e) {
            Common.Log("GameHost Init failed!" + GameState.launchversion);
            Common.CatchException(e);
            GameState.isInit = true;
        }
    }
}

