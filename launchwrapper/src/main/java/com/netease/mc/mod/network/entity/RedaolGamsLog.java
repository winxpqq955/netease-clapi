/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.entity;

import com.netease.mc.mod.network.entity.BaseLog;

public class RedaolGamsLog
extends BaseLog {
    private String msg;

    public RedaolGamsLog(int id, String message) {
        this.uid = id;
        this.type = "gams";
        this.msg = message;
    }
}

