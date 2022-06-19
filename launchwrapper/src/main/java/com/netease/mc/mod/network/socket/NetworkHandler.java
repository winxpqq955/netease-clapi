/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.socket;

import com.netease.mc.mod.network.message.reply.MessageReply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.util.HashMap;

public class NetworkHandler {
    public static NetworkHandler networkHandler = new NetworkHandler();
    public static final int SMIDLEN = 2;
    public static HashMap<Integer, MessageReply> replyHashMap = new HashMap();
    public static HashMap<Integer, MessageReply> replyAsyncHashMap = new HashMap();
    private static MessageRequest request = new MessageRequest();

    private int getSidMid(String msg) {
        if (msg.length() < 2) {
            return -1;
        }
        return msg.charAt(0) * 256 + msg.charAt(1);
    }

    public void register(int smid, MessageReply mrp) {
        replyHashMap.put(smid, mrp);
    }

    public void registerAsync(int smid, MessageReply mrp) {
        replyAsyncHashMap.put(smid, mrp);
    }

    public static void send(int smid, Object ... args) {
        request.send(smid, args);
    }
}

