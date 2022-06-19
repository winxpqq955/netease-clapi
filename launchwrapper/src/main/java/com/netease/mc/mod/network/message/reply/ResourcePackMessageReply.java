/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.message.reply;

import com.netease.mc.mod.network.game.GameUtils;
import com.netease.mc.mod.network.message.reply.Reply;

public class ResourcePackMessageReply
extends Reply {
    public static final int SMID = 4353;
    public String data;

    public void handler(String msg) {
        GameUtils.getNativeMCRespacks(false);
    }
}

