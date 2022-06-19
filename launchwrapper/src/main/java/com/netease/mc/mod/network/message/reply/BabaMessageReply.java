/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package com.netease.mc.mod.network.message.reply;

import com.google.gson.Gson;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.entity.ReflectionMapName;
import com.netease.mc.mod.network.game.GameUtils;
import com.netease.mc.mod.network.http.HttpUtils;
import com.netease.mc.mod.network.message.reply.Reply;

public class BabaMessageReply
extends Reply {
    public static final int SMID = 3;

    public void handler(String mapJson, String url) {
        Common.Log("[HEARTBEAT]recv baba message from wpflaunch");
        Gson gson = new Gson();
        HttpUtils.WEBSVR = url;
        Common.Log("web_url:" + url);
        try {
            GameUtils.mapName = (ReflectionMapName)gson.fromJson(mapJson, ReflectionMapName.class);
            Common.Log(GameUtils.mapName.toString());
        }
        catch (Exception e) {
            Common.CatchException(e);
        }
    }
}

