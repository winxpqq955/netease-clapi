/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.entity;

import com.netease.mc.mod.network.entity.BaseLog;

public class ClientLog
extends BaseLog {
    public String launchversion;

    public ClientLog(int id, String version) {
        this.type = "wpflauncher-dead";
        this.launchversion = version;
        this.uid = id;
    }
}

