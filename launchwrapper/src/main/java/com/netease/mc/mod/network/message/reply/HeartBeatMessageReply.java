/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.message.reply;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.TimerTaskHeartBeat;
import com.netease.mc.mod.network.message.reply.Reply;

public class HeartBeatMessageReply
extends Reply {
    public static final int SMID = 2;

    public void handler(String msg) {
        Common.Log("[HEARTBEAT]recv heartbeat from wpflaunch");
        TimerTaskHeartBeat.lastActiveTimeStamp = Common.getSystemTimeStamp();
        Common.Log("[HEARTBEAT]lastActiveTimeStamp:" + TimerTaskHeartBeat.lastActiveTimeStamp);
    }
}

