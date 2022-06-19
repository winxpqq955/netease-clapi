/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.netease.mc.mod.network.message.reply;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.LittleEndian;
import com.netease.mc.mod.network.message.reply.MessageReply;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reply
implements MessageReply {
    protected final Logger logger = LogManager.getLogger();
    public static final int SMID = 0;
    public static final String HANDLER = "handler";
    private Method handlerMethod = this.gethandler();
    DataInputStream stream = null;
    Map<String, Method> getDataMap = new HashMap<String, Method>();

    public Reply() {
        try {
            this.getDataMap.put("byte", this.getClass().getMethod("getByte", new Class[0]));
            this.getDataMap.put(Byte.class.getName(), this.getClass().getMethod("getByte", new Class[0]));
            this.getDataMap.put("boolean", this.getClass().getMethod("getBoolean", new Class[0]));
            this.getDataMap.put(Boolean.class.getName(), this.getClass().getMethod("getBoolean", new Class[0]));
            this.getDataMap.put("short", this.getClass().getMethod("getShort", new Class[0]));
            this.getDataMap.put(Short.class.getName(), this.getClass().getMethod("getShort", new Class[0]));
            this.getDataMap.put("int", this.getClass().getMethod("getInt", new Class[0]));
            this.getDataMap.put(Integer.class.getName(), this.getClass().getMethod("getInt", new Class[0]));
            this.getDataMap.put("long", this.getClass().getMethod("getLong", new Class[0]));
            this.getDataMap.put(Long.class.getName(), this.getClass().getMethod("getLong", new Class[0]));
            this.getDataMap.put("float", this.getClass().getMethod("getFloat", new Class[0]));
            this.getDataMap.put(Float.class.getName(), this.getClass().getMethod("getFloat", new Class[0]));
            this.getDataMap.put("double", this.getClass().getMethod("getDouble", new Class[0]));
            this.getDataMap.put(Double.class.getName(), this.getClass().getMethod("getDouble", new Class[0]));
            this.getDataMap.put("String", this.getClass().getMethod("getString", new Class[0]));
            this.getDataMap.put(String.class.getName(), this.getClass().getMethod("getString", new Class[0]));
        }
        catch (Exception e) {
            Common.CatchException(e);
        }
    }

    public byte getByte() throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        return this.stream.readByte();
    }

    public boolean getBoolean() throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        return this.stream.readBoolean();
    }

    public short getShort() throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        return (short)LittleEndian.littleShort(this.stream.readShort());
    }

    public int getInt() throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        return LittleEndian.littleInt(this.stream.readInt());
    }

    public long getLong() throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        return LittleEndian.littleLong(this.stream.readLong());
    }

    public float getFloat() throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        return this.stream.readFloat();
    }

    public double getDouble() throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        byte[] bytes = new byte[8];
        this.stream.read(bytes, 0, 8);
        double d = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        return d;
    }

    public String getString() throws Exception {
        if (this.stream == null) {
            throw new IOException();
        }
        int length = LittleEndian.littleShort(this.stream.readUnsignedShort());
        byte[] tmp = new byte[length];
        this.stream.readFully(tmp);
        return new String(tmp, "UTF-8");
    }

    public Method gethandler() {
        Method[] methods;
        for (Method method : methods = this.getClass().getMethods()) {
            if (method.getName().compareTo(HANDLER) != 0) continue;
            return method;
        }
        return null;
    }

    @Override
    public void handMessage(String msg) {
        if (this.handlerMethod == null) {
            return;
        }
        try {
            byte[] bytes = msg.getBytes("ISO-8859-1");
            ByteArrayInputStream bstream = new ByteArrayInputStream(bytes);
            this.stream = new DataInputStream(bstream);
            int smid = this.stream.readUnsignedShort();
            if ((Integer)this.getClass().getField("SMID").get(null) != smid) {
                return;
            }
            Class<?>[] parameterTypes = this.handlerMethod.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; ++i) {
                String parameterName = parameterTypes[i].getName();
                args[i] = this.getDataMap.get(parameterName).invoke(this, new Object[0]);
                if (!Common.isDebugger) continue;
                this.logger.info("[Network] " + parameterName + " " + i + " " + args[i].toString());
            }
            this.handlerMethod.invoke(this, args);
        }
        catch (Exception e) {
            Common.CatchException(e);
        }
    }
}

