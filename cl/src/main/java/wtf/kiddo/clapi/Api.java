/*
BSD 2-Clause License

Copyright (c) 2022, 抽不到绫华小姐不改名

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package wtf.kiddo.clapi;

import com.mojang.authlib.AuthenticationCpp;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;

public class Api {

    public static String Version = "winxpqq955";

    public static AuthenticationCpp auth;
    public static int socketPort = 13337;
    public static String token;
    public static String uuid;
    public static String Uname;

    public static int neteaseControlPort;

    public static void fuckNeteaseBox() {
        try {
            final Field authField = YggdrasilMinecraftSessionService.class.getDeclaredField("auth");
            authField.setAccessible(true);
            auth = (AuthenticationCpp) authField.get(YggdrasilMinecraftSessionService.class);
            String line = System.getProperty("sun.java.command");
            uuid = line.substring(line.lastIndexOf("--uuid ") + 7, line.indexOf(" --accessToken")).replace(" ", "");
            token = line.substring(line.lastIndexOf("--accessToken ") + 14, line.lastIndexOf("--accessToken ") + 47).replace(" ", "");
            Uname = line.substring(line.lastIndexOf("--username ") + 11, line.indexOf(" --version")).replace(" ", "");
            neteaseControlPort = Integer.parseInt(System.getProperty("launcherControlPort"));
            JOptionPane.showMessageDialog(null,
                    "Name:" + Uname + "\n" +
                            "NeteaseControlPort:" + neteaseControlPort + "\n" +
                            "UUID:" + uuid + "\n" +
                            "Token:" + token + "\n" +
                            "Listener Port:" + socketPort + "\n"
                    , "", 0);
            ServerSocket serverSocket = new ServerSocket(socketPort);
            while (true) {
                Socket client = serverSocket.accept();
                new Listener(client);
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
