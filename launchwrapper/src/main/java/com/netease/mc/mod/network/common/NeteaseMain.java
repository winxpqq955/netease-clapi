/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.common;

import com.netease.mc.mod.network.common.GameHost;
import com.netease.mc.mod.network.socket.NetworkSocket;

public class NeteaseMain {
    public static void init(String[] args) {
        GameHost.init(args);
        NetworkSocket.init();
    }
}

