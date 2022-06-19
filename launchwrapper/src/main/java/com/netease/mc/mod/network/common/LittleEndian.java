/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.common;

public class LittleEndian {
    public static int littleShort(int number) {
        return (number & 0xFF) << 8 | number >> 8 & 0xFF;
    }

    public static int littleInt(int number) {
        return (number & 0xFF) << 24 | (number & 0xFF00) << 8 | number >> 8 & 0xFF00 | number >> 24 & 0xFF;
    }

    public static long littleLong(long number) {
        return (number & 0xFFL) << 56 | (number & 0xFF00L) << 40 | (number & 0xFF0000L) << 24 | (number & 0xFF000000L) << 8 | number >> 8 & 0xFF000000L | number >> 24 & 0xFF0000L | number >> 40 & 0xFF00L | number >> 56 & 0xFFL;
    }
}

