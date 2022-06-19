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

import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.common.Library;
import net.minecraft.launchwrapper.Launch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;

public class Listener implements Runnable {
    private Socket socket;

    public Listener(Socket socket) {
        this.socket = socket;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            try {
                DataInputStream input = new DataInputStream(this.socket.getInputStream());
                String clientInputStr = input.readUTF();
                if (clientInputStr.contains("CL7|")) {
                    System.out.println("[消息] 请求:" + this.socket.getRemoteSocketAddress() + "|" + clientInputStr);
                    DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
                    if (clientInputStr.contains("Connect")) {
                        try {
                            //clientInputStr.split("\\|")[2] is serverid
                            final Class  nativeClass = Launch.classLoader.loadClass("nativeTransformer");
                            final Field field_str = nativeClass.getDeclaredField("str");
                            field_str.setAccessible(true);
                            field_str.set(field_str,clientInputStr.split("\\|")[2]);
                            Library.Test();
                            Api.auth.Authentication(Api.neteaseControlPort, clientInputStr.split("\\|")[2]);
                        }catch (Exception e) {
                            out.writeUTF("Failed|" + Api.Version);
                            e.printStackTrace();
                            out.close();
                            input.close();
                        }
                        out.writeUTF("Ok|" + GameState.gameid + "|" + GameState.launcherport);
                        out.close();
                        input.close();
                    }
                }
                this.socket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    this.socket.close();
                }
                catch (Exception e2) {
                    this.socket = null;
                }
            }
        } finally {
            if (this.socket != null) {
                try {
                    this.socket.close();
                }
                catch (Exception e) {
                    this.socket = null;
                }
            }
        }
    }
}
