/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.netease.mc.mod.network.socket;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.Encrption;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.common.LittleEndian;
import com.netease.mc.mod.network.common.TimerTaskHeartBeat;
import com.netease.mc.mod.network.message.reply.BabaMessageReply;
import com.netease.mc.mod.network.message.reply.HeartBeatMessageReply;
import com.netease.mc.mod.network.message.reply.ModListMessageReply;
import com.netease.mc.mod.network.message.reply.ResourcePackMessageReply;
import com.netease.mc.mod.network.socket.NetworkHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSocket {
    private static final Logger logger = LogManager.getLogger();
    public static final String MSG_QUIT = "quit";
    private static final String SEVER_IP = "127.0.0.1";
    private static final String CLOSECMD = "\u0000\u0001";
    private static final short LOGIN_CMD = 0;
    private static final int BUFFER_SIZE = 20480;
    public static Socket mSocket;
    public static MsgQueue<String> mSendMsgQueue;
    public static MsgQueue<String> mRecvMsgQueue;
    public static char[] tmp;
    private static int server_port;
    public static boolean isLogin;
    public static Encrption encrptHandler;
    public static Timer timer;
    private static int HEAT_BEAT_INTERVAL;
    public static final int SMIDLEN = 2;
    private static boolean socketClosedRemote;

    public static void init() {
        mSendMsgQueue = new MsgQueue();
        mRecvMsgQueue = new MsgQueue();
        try {
            if (GameState.launcherport != 0) {
                server_port = GameState.launcherport;
            }
            Common.Log("My socket: 127.0.0.1:" + server_port);
            mSocket = new Socket(SEVER_IP, server_port);
            NetworkSocket.connect();
            encrptHandler = new Encrption();
            NetworkHandler.send(0, GameState.gameid, encrptHandler.GetSendToken());
            NetworkHandler.networkHandler.register(3, new BabaMessageReply());
            NetworkHandler.networkHandler.register(4352, new ModListMessageReply());
            NetworkHandler.networkHandler.register(4353, new ResourcePackMessageReply());
            if (!GameState.launchversion.equals("")) {
                timer = new Timer();
                timer.schedule(new TimerTaskHeartBeat(), HEAT_BEAT_INTERVAL, (long)HEAT_BEAT_INTERVAL);
                NetworkHandler.networkHandler.register(2, new HeartBeatMessageReply());
            }
            Common.Log("My socket: " + mSocket);
        }
        catch (Exception e) {
            NetworkSocket.onSocketConnectFail();
        }
    }

    public static void connect() {
        new Thread((Runnable)new Sender(), "Sender").start();
        new Thread((Runnable)new Receiver(), "Receiver").start();
    }

    public static void sendMesg(String msg) {
        if (Common.isDebugger) {
            logger.debug("[Network]send message:" + msg);
        }
        NetworkSocket.pushSendMsg(msg);
    }

    public static final void pushSendMsg(String msg) {
        mSendMsgQueue.push(msg);
    }

    private static int getSidMid(String msg) {
        if (msg.length() < 2) {
            return -1;
        }
        return msg.charAt(0) * 256 + msg.charAt(1);
    }

    public static final void pushRecvMsg(String msg) {
        int smid = NetworkSocket.getSidMid(msg);
        if (!NetworkHandler.replyAsyncHashMap.containsKey(smid)) {
            mRecvMsgQueue.push(msg);
            return;
        }
        Common.Log("smid:" + smid);
        NetworkHandler.replyAsyncHashMap.get(smid).handMessage(msg);
    }

    public static final void quit() {
        NetworkSocket.pushSendMsg(MSG_QUIT);
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final Socket getSocket() {
        return mSocket;
    }

    private static void onLog(String msg) {
        byte[] bytes;
        try {
            bytes = msg.getBytes("ISO-8859-1");
        }
        catch (Exception e) {
            Common.Log("MSG is wrong! ");
            return;
        }
        String tem = "";
        for (int i = 0; i < bytes.length && i < 2; ++i) {
            tem = tem + bytes[i];
            tem = tem + "  ";
        }
        Common.Log(tem);
    }

    protected static void onMsgSendStart(String msg) {
        Common.Log("[ME]: ");
        NetworkSocket.onLog(msg);
    }

    protected static void onMsgReceived(String msg) {
        Common.Log("[SERVER]: ");
        NetworkSocket.onLog(msg);
        NetworkSocket.pushRecvMsg(msg);
    }

    protected static void onSocketClosedRemote() {
        socketClosedRemote = true;
        Common.Log("Remote socket closed, input any words to quit.");
        NetworkSocket.closeMinecraft();
    }

    private static void onSocketConnectFail() {
        LogManager.getLogger().info("My socket: socket connect fail");
        Common.Log("Close Game");
        NetworkSocket.closeMinecraft();
    }

    private static void onSocketClosedSelf() {
        LogManager.getLogger().info("Socket Close!");
        if (mSocket != null) {
            try {
                mSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
        Common.Log("Close Game");
        NetworkSocket.closeMinecraft();
    }

    protected static void onMsgSendEnd(String msg, boolean success) {
    }

    protected static void onMsgInput(String msg) {
    }

    private static void closeMinecraft() {
        mRecvMsgQueue.push(CLOSECMD);
    }

    static {
        tmp = new char[20480];
        server_port = 9876;
        isLogin = false;
        encrptHandler = null;
        HEAT_BEAT_INTERVAL = 60000;
        socketClosedRemote = false;
    }

    private static class Receiver
    implements Runnable {
        private Receiver() {
        }

        @Override
        public void run() {
            Common.Log("Receiver ... start --- " + Thread.currentThread().getName());
            try {
                DataInputStream in = new DataInputStream(mSocket.getInputStream());
                String msg = "";
                while (mSocket != null) {
                    int length = in.readUnsignedByte();
                    byte[] tmp = new byte[length += in.readUnsignedByte() * 256];
                    for (int i = 0; i < length; ++i) {
                        tmp[i] = in.readByte();
                    }
                    byte[] plaintext = encrptHandler.Decrypt(tmp);
                    msg = new String(plaintext, "ISO-8859-1");
                    NetworkSocket.onMsgReceived(msg);
                }
                in.close();
                NetworkSocket.pushSendMsg(NetworkSocket.MSG_QUIT);
                NetworkSocket.onSocketClosedRemote();
            }
            catch (IOException e) {
                NetworkSocket.onSocketClosedSelf();
            }
            Common.Log("Receiver ... end --- " + Thread.currentThread().getName());
        }
    }

    private static class Sender
    implements Runnable {
        private Sender() {
        }

        @Override
        public void run() {
            Common.Log("Sender ... start --- " + Thread.currentThread().getName());
            try {
                DataOutputStream out = new DataOutputStream(mSocket.getOutputStream());
                String msg = "";
                while (!(msg = mSendMsgQueue.pop()).equals(NetworkSocket.MSG_QUIT)) {
                    NetworkSocket.onMsgSendStart(msg);
                    byte[] data = null;
                    if (isLogin) {
                        data = encrptHandler.Encrypt(msg.getBytes("ISO-8859-1"));
                    } else {
                        data = msg.getBytes("ISO-8859-1");
                        isLogin = true;
                    }
                    out.writeShort(LittleEndian.littleShort(data.length));
                    out.write(data);
                    out.flush();
                    NetworkSocket.onMsgSendEnd(msg, true);
                }
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                NetworkSocket.onSocketClosedSelf();
            }
            Common.Log("Sender ... end --- " + Thread.currentThread().getName());
        }
    }

    public static class MsgQueue<T> {
        private static final int CAPACITY = 100;
        private List<T> mMsgs = new ArrayList<T>();

        public synchronized void push(T msg) {
            try {
                while (this.mMsgs.size() >= 100) {
                    this.wait();
                }
                this.mMsgs.add(msg);
                this.notifyAll();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized T pop() {
            T msg = null;
            try {
                while (this.mMsgs.size() <= 0) {
                    this.wait();
                }
                msg = this.mMsgs.get(0);
                this.mMsgs.remove(0);
                this.notifyAll();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return msg;
        }

        public int count() {
            return this.mMsgs.size();
        }
    }
}

