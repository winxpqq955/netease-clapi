/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.common;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.game.GameUtils;
import com.netease.mc.mod.network.socket.NetworkHandler;
import com.netease.mc.mod.network.socket.NetworkSocket;
import java.util.TimerTask;

public class TimerTaskHeartBeat
extends TimerTask {
    public static long lastActiveTimeStamp = Common.getSystemTimeStamp();

    @Override
    public void run() {
        long current = Common.getSystemTimeStamp();
        if (Math.abs(current - lastActiveTimeStamp) > 300L) {
            GameUtils.getMama();
            if (NetworkSocket.timer != null) {
                NetworkSocket.timer.cancel();
                NetworkSocket.timer.purge();
            }
            return;
        }
        NetworkHandler.send(2, 0);
        Common.Log("[HEARTBEAT]send heartbeat");
        GameUtils.getRedaolComps();
        GameUtils.getNativeMCRespacks();
    }
}

