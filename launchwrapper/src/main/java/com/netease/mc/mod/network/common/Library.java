/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.netease.mc.mod.network.common;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Library {
    private static final char[] HEX_CHAR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final HashMap<String, String> X86dlls = new HashMap<String, String>(){
        {
            this.put("api-ms-win-core-util-l1-1-0.dll", "2886C75F8B9D3EFDF315C44B52847AEE");
            this.put("api-ms-win-core-timezone-l1-1-0.dll", "A20084F41B3F1C549D6625C790B72268");
            this.put("api-ms-win-core-errorhandling-l1-1-0.dll", "906CB0C8ABA8342D552B0F37DDFD475F");
            this.put("msvcp140.dll", "CFBDF284C12056347E6773CB3949FBBA");
            this.put("api-ms-win-core-debug-l1-1-0.dll", "584766DF684B2AD2A3A5B05A5B457FAC");
            this.put("api-ms-win-crt-convert-l1-1-0.dll", "5245F303E96166B8E625DD0A97E2D66A");
            this.put("api-ms-win-crt-multibyte-l1-1-0.dll", "809BC1010EAF714CD095189AF236CE2F");
            this.put("api-ms-win-core-processthreads-l1-1-0.dll", "39047E168FFBDD19185504633D6ECA29");
            this.put("api-ms-win-core-localization-l1-2-0.dll", "3B9D034CA8A0345BC8F248927A86BF22");
            this.put("api-ms-win-crt-time-l1-1-0.dll", "1FA7C2B81CDFD7ACE42A2A9A0781C946");
            this.put("api-ms-win-core-libraryloader-l1-1-0.dll", "44CA070DC5C09FF8588CF6CDCB64E7A2");
            this.put("api-ms-win-core-console-l1-1-0.dll", "37DA7F6961082DD96A537235DD89B114");
            this.put("api-ms-win-core-heap-l1-1-0.dll", "1CD8672D8C08B39560A9D5518836493E");
            this.put("api-ms-win-core-file-l1-2-0.dll", "F6D1216E974FB76585FD350EBDC30648");
            this.put("msvcr120.dll", "034CCADC1C073E4216E9466B720F9849");
            this.put("api-ms-win-crt-string-l1-1-0.dll", "5E72659B38A2977984BBC23ED274F007");
            this.put("vcruntime140.dll", "8E65E033799EB9FD46BC5C184E7D1B85");
            this.put("api-ms-win-crt-filesystem-l1-1-0.dll", "AB8734C2328A46E7E9583BEFEB7085A2");
            this.put("api-ms-win-crt-environment-l1-1-0.dll", "45C54A21261180410091CEFB23F6A5AE");
            this.put("api-ms-win-crt-locale-l1-1-0.dll", "E70D8FE9D21841202B4FD1CF55D37AC5");
            this.put("WinlicenseSDK.dll", "D90C96DBFA873779F2327CBD35DA282D");
            this.put("libssl-1_1.dll", "CF3B0A37E73A1332DC6E6E5B53A0C3DF");
            this.put("api-ms-win-crt-stdio-l1-1-0.dll", "32D7B95B1BCE23DB9FBD0578053BA87F");
            this.put("api-ms-win-crt-utility-l1-1-0.dll", "D6ABF5C056D80592F8E2439E195D61AC");
            this.put("api-ms-win-crt-runtime-l1-1-0.dll", "AE3FA6BF777B0429B825FB6B028F8A48");
            this.put("api-ms-win-crt-math-l1-1-0.dll", "D0D380AF839124368A96D6AA82C7C8AE");
            this.put("api-ms-win-core-handle-l1-1-0.dll", "FC68978ABB44E572DFE637B7DD3D615F");
            this.put("SecureEngineSDK32.dll", "B947E763E6E47DC79C68294E1CA8CABF");
            this.put("api-ms-win-core-profile-l1-1-0.dll", "7697F94ED76B22D83D677B999EDFC2E1");
            this.put("api-ms-win-core-synch-l1-1-0.dll", "88C4CA509C947509E123F22E5F077639");
            this.put("api-ms-win-crt-heap-l1-1-0.dll", "39D81596A7308E978D67AD6FDCCDD331");
            this.put("api-ms-win-core-memory-l1-1-0.dll", "FC13F11A2458879B23C87B29C2BAD934");
            this.put("api-ms-win-core-synch-l1-2-0.dll", "F6B4D8D403D22EB87A60BF6E4A3E7041");
            this.put("api-ms-win-core-file-l2-1-0.dll", "BFB08FB09E8D68673F2F0213C59E2B97");
            this.put("api-ms-win-crt-process-l1-1-0.dll", "8F8A47617DFD829A63E3EC4AFF2718D9");
            this.put("api-ms-win-crt-private-l1-1-0.dll", "1DD5666125B8734E92B1041139FA6C37");
            this.put("api-ms-win-core-namedpipe-l1-1-0.dll", "07954AF744363F9807355E4E9408DF45");
            this.put("api-ms-win-core-processenvironment-l1-1-0.dll", "39556E904FA2405ABAF27231DA8EF9E5");
            this.put("api-ms-win-core-processthreads-l1-1-1.dll", "C2EAD5FCCE95A04D31810768A3D44D57");
            this.put("api-ms-win-core-sysinfo-l1-1-0.dll", "B9EA058418BE64F85B0FF62341F7099E");
            this.put("ucrtbase.dll", "3E0303F978818E5C944F5485792696FD");
            this.put("api-ms-win-core-datetime-l1-1-0.dll", "3B3BD0AD4FEA16AB58FCAEAE4629879C");
            this.put("libcrypto-1_1.dll", "6F52970D7A41141D3C93B3937BD6B84C");
            this.put("api-ms-win-core-file-l1-1-0.dll", "779A8B14C22E463EA535CBCA9EA84D49");
            this.put("api-ms-win-core-rtlsupport-l1-1-0.dll", "FDF0B4BF0214585E18EE2F6978F985B0");
            this.put("api-ms-win-core-interlocked-l1-1-0.dll", "B8BB783DEE4EA95576882625C365E616");
            this.put("api-ms-win-core-string-l1-1-0.dll", "687533A89B43510CCE4D8B2ECB261AA0");
            this.put("api-ms-win-crt-conio-l1-1-0.dll", "3B038338C1EB179D8EEE3883CF42BC3E");
        }
    };
    private static final HashMap<String, String> X64dlls = new HashMap<String, String>(){
        {
            this.put("api-ms-win-core-util-l1-1-0.dll", "7A664D454E9675CB3AAC9F7C5A7B32B3");
            this.put("api-ms-win-core-timezone-l1-1-0.dll", "EB0F5DB36D2BF12C0B2621FC47CE7DE6");
            this.put("api-ms-win-core-errorhandling-l1-1-0.dll", "31990AAAB1AEEAE6BFF96EAF3809EDA9");
            this.put("msvcp140.dll", "B9ABE16B723DDD90FC612D0DDB0F7AB4");
            this.put("api-ms-win-core-debug-l1-1-0.dll", "F91F7DC238DE2C03DC64AA5D2E3B4E49");
            this.put("api-ms-win-crt-convert-l1-1-0.dll", "66DF85E9AD3EAC3E1C3CF5BBECC1A62A");
            this.put("api-ms-win-crt-multibyte-l1-1-0.dll", "D484455EFCF6BE3063EB97362F9901F3");
            this.put("api-ms-win-core-processthreads-l1-1-0.dll", "C12244DB4C14058F457BFB3B9A1FD21F");
            this.put("api-ms-win-core-localization-l1-2-0.dll", "1831740F8B864FB3ACD65EE5B9B2B7B5");
            this.put("api-ms-win-crt-time-l1-1-0.dll", "ABDA7B543935BE9365BBA8B837CD5D7F");
            this.put("api-ms-win-core-libraryloader-l1-1-0.dll", "3E3A777CAD2AAFDE613836EE88179A58");
            this.put("api-ms-win-core-console-l1-1-0.dll", "E4A519EF5D0A378EA82C423FE1E4586E");
            this.put("api-ms-win-core-heap-l1-1-0.dll", "11A672968CE4879767AFAF573E4EDE0C");
            this.put("api-ms-win-core-file-l1-2-0.dll", "D2A653644B6FFF60EA4E01C17D72BC72");
            this.put("msvcr120.dll", "9C861C079DD81762B6C54E37597B7712");
            this.put("libcrypto-1_1-x64.dll", "7D38A7D17B739D762EC51C8FD5D654BB");
            this.put("api-ms-win-crt-string-l1-1-0.dll", "455D9BB1E9D392F7E2164FCA19EDEAFE");
            this.put("vcruntime140.dll", "238DAE6C4BB494893D01B99F6EFFDB93");
            this.put("api-ms-win-crt-filesystem-l1-1-0.dll", "F3DE66B5F62DF3C4BF1EC19E0D5CD4E6");
            this.put("api-ms-win-crt-environment-l1-1-0.dll", "558F6242D5E6418EEF1BD440B5695F7F");
            this.put("api-ms-win-crt-locale-l1-1-0.dll", "CFA597B94C67EA57EBEBC73EEF81F6B9");
            this.put("api-ms-win-crt-stdio-l1-1-0.dll", "53494843ED7802D88D867BE873CE3D7A");
            this.put("libssl-1_1-x64.dll", "7D4500364E97867838BCB4F5843B4D8B");
            this.put("WinlicenseSDK64.dll", "256211451645EBBC533C51D073C7D8E4");
            this.put("api-ms-win-crt-utility-l1-1-0.dll", "4162803447D62C51A125721334ACED3F");
            this.put("api-ms-win-crt-runtime-l1-1-0.dll", "9936474F1B84FE5F422AC865D805C395");
            this.put("api-ms-win-crt-math-l1-1-0.dll", "136725B9E73AE8971FC5984FB782E92E");
            this.put("api-ms-win-core-handle-l1-1-0.dll", "968C1759F5D4AA2BED859A2DF67ACC8F");
            this.put("api-ms-win-core-profile-l1-1-0.dll", "F8A266BA5362A17C89DF60B5B5EBEF41");
            this.put("api-ms-win-core-synch-l1-1-0.dll", "FC2E64B5EEA906D30694DB192603B21F");
            this.put("api-ms-win-crt-heap-l1-1-0.dll", "4A96D00BCEDEB2F3AB74F319C68EB8FA");
            this.put("api-ms-win-core-xstate-l2-1-0.dll", "8ED3D95C3337BEB4624DE5B93E650C75");
            this.put("api-ms-win-core-memory-l1-1-0.dll", "D14C0B3BC3032A043DDFFBC39D26DB7C");
            this.put("api-ms-win-core-synch-l1-2-0.dll", "86D5821B72DE71722001E9558B89C242");
            this.put("api-ms-win-core-file-l2-1-0.dll", "4BBD0D70A15B8859EB2912D30C89ACD7");
            this.put("api-ms-win-crt-process-l1-1-0.dll", "9BB2FBBEE0215357441A9BFEE8556095");
            this.put("api-ms-win-crt-private-l1-1-0.dll", "7D6B3AA7FF9A3EE808CA56E8540890A1");
            this.put("api-ms-win-core-namedpipe-l1-1-0.dll", "627327251BC258AA258848DE32B698BA");
            this.put("api-ms-win-core-processenvironment-l1-1-0.dll", "F4F8BD9A68CECFF1D22204D29CF8A914");
            this.put("api-ms-win-core-processthreads-l1-1-1.dll", "C7BB61041BD92B0D269D7DCC10D17A11");
            this.put("api-ms-win-core-sysinfo-l1-1-0.dll", "178EAF8111DFDD995E15FD2E3F4545CB");
            this.put("ucrtbase.dll", "D6181DE1FCD6289D22022B83EF2BF09D");
            this.put("api-ms-win-core-datetime-l1-1-0.dll", "B3F46F0820B641C5A9A9D0A4BFC94355");
            this.put("SecureEngineSDK64.dll", "023CA3F56CE9D9AFF9E4839301E82C82");
            this.put("api-ms-win-core-file-l1-1-0.dll", "B90485EB6D2E835F975C6F1011BE880F");
            this.put("api-ms-win-core-rtlsupport-l1-1-0.dll", "FDB1476DCB99850F82B9567956A7A46B");
            this.put("api-ms-win-core-interlocked-l1-1-0.dll", "7EE0013D07ED45C081DF41E64AB14889");
            this.put("api-ms-win-core-string-l1-1-0.dll", "6BA0B8044F21A5C0086F5B8D2AC15D89");
            this.put("api-ms-win-crt-conio-l1-1-0.dll", "5DFDB30E4C7A4CB94C354017A0335410");
        }
    };
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Library lib = new Library();

    private static Boolean SafeLoadLibrary(String path) {
        try {
            System.load(path);
            return true;
        }
        catch (Throwable e) {
            LOGGER.info("error", e);
            return false;
        }
    }

    public static Boolean LoadLibrary() throws Exception {
        try {
            String javaLibPath = System.getProperty("java.library.path");
            File runtime = new File(javaLibPath, "runtime");
            File[] files = runtime.listFiles();
            ArrayList<File> failedFiles = new ArrayList<File>();
            for (int i = 0; i < 10 && (i <= 0 || files.length > 0); ++i) {
                for (File file : files) {
                    if (Library.checkFile(file)) {
                        if (Library.SafeLoadLibrary(file.getPath()).booleanValue()) continue;
                        failedFiles.add(file);
                        continue;
                    }
                    failedFiles.add(file);
                }
                files = failedFiles.toArray(new File[failedFiles.size()]);
                failedFiles.clear();
            }
        }
        catch (Throwable e) {
            LogManager.getLogger().info("load dll failed!");
            Common.CatchException(e);
        }
        return true;
    }

    private static boolean checkFile(File file) {
        String name = file.getName();
        if (name.equals("api-ms-win-crt-utility-l1-1-1.dll")) {
            return true;
        }
        String md5 = "";
        if (X64dlls.containsKey(name)) {
            if (md5.isEmpty()) {
                md5 = Library.getMD5(file);
            }
            if (X64dlls.get(name).equals(md5)) {
                return true;
            }
        }
        if (X86dlls.containsKey(name)) {
            if (md5.isEmpty()) {
                md5 = Library.getMD5(file);
            }
            if (X86dlls.get(name).equals(md5)) {
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            int length;
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            String string = Library.ToHexString(MD5.digest()).toUpperCase();
            return string;
        }
        catch (Exception e) {
            e.printStackTrace();
            String string = null;
            return string;
        }
        finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] DecryptClass(byte[] input) {
        if (input.length <= 0 || input[0] == -54) {
            return input;
        }
        byte[] output = lib.DecryptClassBytes(input);
        return output;
    }

    private static boolean byteArrayEqual(byte[] bs1, byte[] bs2) {
        if (bs1.length != bs2.length) {
            return false;
        }
        for (int i = 0; i < bs1.length; ++i) {
            if (bs1[i] == bs2[i]) continue;
            return false;
        }
        return true;
    }

    private static String logBytes(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append(bytes.length);
        stringBuilder.append(' ');
        for (int n : bytes) {
            int a = n < 0 ? 256 + n : n;
            stringBuilder.append(HEX_CHAR[a / 16]);
            stringBuilder.append(HEX_CHAR[a % 16]);
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    private static String ToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        for (int n : bytes) {
            int a = n < 0 ? 256 + n : n;
            stringBuilder.append(HEX_CHAR[a / 16]);
            stringBuilder.append(HEX_CHAR[a % 16]);
        }
        return stringBuilder.toString();
    }

    public static void Log(String message) {
        try {
            if (GameState.needCheck()) {
                Library.log(message);
            }
        }
        catch (Throwable e) {
            Common.CatchException(e);
        }
    }

    public static boolean Init() {
        try {
            return Library.init();
        }
        catch (Throwable e) {
            Common.CatchException(e);
            return false;
        }
    }

    public static void PreInit() {
        try {
            Class<?> t = Class.forName(GameState.class.getName(), true, Launch.classLoader);
            Boolean needCheck = (Boolean)t.getMethod("needCheck", new Class[0]).invoke(null, new Object[0]);
            if (needCheck.booleanValue()) {
                Library.preInit();
            }
        }
        catch (Throwable e) {
            Common.CatchException(e);
        }
    }

    public static void Test() {
        try {
            Library.test();
        }
        finally {
            return;
        }
    }

    public static void Close() {
        try {
            Library.close();
        }
        catch (Throwable e) {
            Common.CatchException(e);
        }
    }

    public native byte[] DecryptClassBytes(byte[] var1);

    public static native void GetToken(byte[] var0, int var1);

    public static native long NewChaCha(int var0, byte[] var1);

    public static native void DeleteChaCha(long var0);

    public static native void ChaChaProcess(long var0, byte[] var2, int var3);

    public static native int Skip32(boolean var0, byte[] var1, int var2);

    public static native boolean init();

    public static native void preInit();

    public static native void log(String var0);

    public static native void close();

    public static native void test();
}

