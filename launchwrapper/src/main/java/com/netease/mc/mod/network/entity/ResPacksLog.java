/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.entity;

import com.netease.mc.mod.network.entity.BaseLog;

public class ResPacksLog
extends BaseLog {
    private String msg;

    public ResPacksLog(int id, String message) {
        this.uid = id;
        this.type = "kcap";
        this.msg = message;
    }
}

