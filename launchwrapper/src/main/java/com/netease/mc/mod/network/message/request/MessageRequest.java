/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.message.request;

import com.netease.mc.mod.network.common.LittleEndian;
import com.netease.mc.mod.network.socket.NetworkSocket;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MessageRequest {
    public DataOutputStream stream = null;
    Map<String, Method> writeDataMap = new HashMap<String, Method>();

    public MessageRequest() {
        try {
            Class[] argClasses = new Class[]{Byte.TYPE};
            this.writeDataMap.put("byte", this.getClass().getMethod("writeByte", argClasses));
            this.writeDataMap.put(Byte.class.getName(), this.getClass().getMethod("writeByte", argClasses));
            argClasses[0] = Boolean.TYPE;
            this.writeDataMap.put(Boolean.class.getName(), this.getClass().getMethod("writeBoolean", argClasses));
            argClasses[0] = Short.TYPE;
            this.writeDataMap.put("short", this.getClass().getMethod("writeShort", argClasses));
            this.writeDataMap.put(Short.class.getName(), this.getClass().getMethod("writeShort", argClasses));
            argClasses[0] = Integer.TYPE;
            this.writeDataMap.put("int", this.getClass().getMethod("writeInt", argClasses));
            this.writeDataMap.put(Integer.class.getName(), this.getClass().getMethod("writeInt", argClasses));
            argClasses[0] = Long.TYPE;
            this.writeDataMap.put("long", this.getClass().getMethod("writeLong", argClasses));
            this.writeDataMap.put(Long.class.getName(), this.getClass().getMethod("writeLong", argClasses));
            argClasses[0] = Float.TYPE;
            this.writeDataMap.put(Float.class.getName(), this.getClass().getMethod("writeFloat", argClasses));
            argClasses[0] = Double.TYPE;
            this.writeDataMap.put(Double.class.getName(), this.getClass().getMethod("writeDouble", argClasses));
            argClasses[0] = String.class;
            this.writeDataMap.put(String.class.getName(), this.getClass().getMethod("writeString", argClasses));
        }
        catch (Exception e) {
            System.out.print(e.toString());
        }
    }

    public void writeByte(byte b) throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        this.stream.writeByte(b);
    }

    public void writeBoolean(boolean bool) throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        this.stream.writeBoolean(bool);
    }

    public void writeShort(short s) throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        this.stream.writeShort(LittleEndian.littleShort(s));
    }

    public void writeInt(int i) throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        this.stream.writeInt(LittleEndian.littleInt(i));
    }

    public void writeLong(long i) throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        this.stream.writeLong(LittleEndian.littleLong(i));
    }

    public void writeFloat(float f) throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        this.stream.writeFloat(f);
    }

    public void writeDouble(double d) throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        this.stream.writeDouble(d);
    }

    public void writeString(String str) throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        byte[] tmp = str.getBytes("UTF-8");
        int length = tmp.length;
        this.stream.writeShort(LittleEndian.littleShort(length));
        this.stream.write(tmp);
    }

    public void send(int smid, Object ... args) {
        ByteArrayOutputStream bstream = new ByteArrayOutputStream();
        this.stream = new DataOutputStream(bstream);
        try {
            this.stream.writeShort(smid);
            Object[] objects = new Object[1];
            for (int i = 0; i < args.length; ++i) {
                objects[0] = args[i];
                this.writeDataMap.get(args[i].getClass().getName()).invoke(this, objects);
            }
            NetworkSocket.sendMesg(new String(bstream.toByteArray(), "ISO-8859-1"));
            this.stream = null;
        }
        catch (Exception e) {
            this.stream = null;
        }
    }
}

