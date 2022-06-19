/*
 * Decompiled with CFR 0.152.
 */
package com.netease.mc.mod.network.common;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.Library;
import java.io.UnsupportedEncodingException;

public class Encrption {
    public byte[] token;
    public long recv_pointer = 0L;
    public long send_pointer = 0L;

    public Encrption() {
        try {
            this.token = new byte[32];
            Library.GetToken(this.token, 32);
            byte[] sendkey = new byte[32];
            System.arraycopy(this.token, 16, sendkey, 0, 16);
            System.arraycopy(this.token, 0, sendkey, 16, 16);
            this.recv_pointer = Library.NewChaCha(8, this.token);
            this.send_pointer = Library.NewChaCha(8, sendkey);
        }
        catch (Exception e) {
            Common.CatchException(e);
        }
    }

    public String GetSendToken() throws UnsupportedEncodingException {
        return new String(this.token, "ISO-8859-1");
    }

    public byte[] Encrypt(byte[] plaintext) {
        Library.ChaChaProcess(this.send_pointer, plaintext, plaintext.length);
        return plaintext;
    }

    public byte[] Decrypt(byte[] encryptText) {
        Library.ChaChaProcess(this.recv_pointer, encryptText, encryptText.length);
        return encryptText;
    }
}

