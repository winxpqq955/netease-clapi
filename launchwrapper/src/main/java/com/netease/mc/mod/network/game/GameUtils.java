/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package com.netease.mc.mod.network.game;

import com.google.gson.Gson;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.entity.ClientLog;
import com.netease.mc.mod.network.entity.ModEntity;
import com.netease.mc.mod.network.entity.ReflectionMapName;
import com.netease.mc.mod.network.entity.ResourcePackEntity;
import com.netease.mc.mod.network.entity.UrgentLog;
import com.netease.mc.mod.network.http.HttpUtils;
import com.netease.mc.mod.network.message.reply.ModListMessageReply;
import com.netease.mc.mod.network.message.reply.ResourcePackMessageReply;
import com.netease.mc.mod.network.socket.NetworkHandler;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.launchwrapper.Launch;

public class GameUtils {
    public static final String EMPTY_BABA = "[]";
    public static String lastRedaol = "[]";
    public static ReflectionMapName mapName = new ReflectionMapName();

    private static boolean noNeedGetComps() {
        if (null == mapName) {
            return false;
        }
        return !GameUtils.mapName.cn_redaol.equals("") && !GameUtils.mapName.md_get_redaol.equals("") && !GameUtils.mapName.md_redaol_getcomps.equals("") && lastRedaol.equals(EMPTY_BABA);
    }

    public static Class<?> getNativeMCClass() {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(GameUtils.mapName.cn_nativemc, false, Launch.classLoader);
        }
        catch (Exception e) {
            Common.CatchException(e);
        }
        return clazz;
    }

    public static Object getNativeMCInstance() {
        Class<?> clazz = GameUtils.getNativeMCClass();
        if (clazz == null) {
            return null;
        }
        Object result = null;
        try {
            Method instanceMethod = clazz.getMethod(GameUtils.mapName.md_get_nativemc, new Class[0]);
            result = instanceMethod.invoke(null, new Object[0]);
        }
        catch (Exception e) {
            Common.CatchException(e);
        }
        return result;
    }

    public static void getNativeMCRespacks() {
        GameUtils.getNativeMCRespacks(true);
    }

    public static void getNativeMCRespacks(boolean check) {
        Class<?> clazz = GameUtils.getNativeMCClass();
        if (clazz == null) {
            return;
        }
        try {
            Method getrespacksmethod = clazz.getMethod(GameUtils.mapName.md_nativemc_getrpr, new Class[0]);
            Object nativeCM = GameUtils.getNativeMCInstance();
            Object gc = getrespacksmethod.invoke(nativeCM, new Object[0]);
            Class<?> c = gc.getClass();
            Method getRespackMethod = c.getMethod(GameUtils.mapName.md_rpr_getkcaps, new Class[0]);
            List respackList = (List)getRespackMethod.invoke(gc, new Object[0]);
            if (check && respackList.toString() == EMPTY_BABA) {
                return;
            }
            ArrayList<ResourcePackEntity> resourcePackEntities = new ArrayList<ResourcePackEntity>();
            if (respackList != null && respackList.size() > 0) {
                for (int idx = 0; idx < respackList.size(); ++idx) {
                    Class<?> resourcePackClass = respackList.get(idx).getClass();
                    if (resourcePackClass == null) {
                        Common.Log("resourcePackClass is null");
                        break;
                    }
                    ResourcePackEntity entity = new ResourcePackEntity();
                    entity.reResourcePack = respackList.get(idx).toString();
                    Method getTextureDescMethod = resourcePackClass.getMethod("func_110519_e", new Class[0]);
                    Method getPackNameMethod = resourcePackClass.getMethod("func_110515_d", new Class[0]);
                    entity.name = getPackNameMethod.invoke(respackList.get(idx), new Object[0]).toString();
                    entity.desc = getTextureDescMethod.invoke(respackList.get(idx), new Object[0]).toString();
                    resourcePackEntities.add(entity);
                }
            }
            Gson gson = new Gson();
            ResourcePackMessageReply msg = new ResourcePackMessageReply();
            msg.data = gson.toJson(resourcePackEntities);
            NetworkHandler.send(4353, msg.data);
        }
        catch (Exception e) {
            Common.CatchException(e);
        }
    }

    public static void getRedaolComps() {
        GameUtils.getRedaolComps(true);
    }

    public static String Test(boolean check) {
        return "Test  mod";
    }

    public static String getRedaolCompsStrV2(boolean check) {
        try {
            Class<?> t = Class.forName(GameUtils.class.getName(), true, Launch.classLoader);
            Class[] argsClass = new Class[]{Boolean.TYPE};
            return (String)t.getMethod("getRedaolCompsStr", argsClass).invoke(null, check);
        }
        catch (Throwable e) {
            Common.CatchException(e);
            return "";
        }
    }

    public static String getRedaolCompsStr(boolean check) {
        if (check && !GameUtils.noNeedGetComps()) {
            Common.Log("[getRedaolComps]no need getRedaolComps anymore!");
            return null;
        }
        try {
            Class<?> clazz = Class.forName(GameUtils.mapName.cn_redaol);
            Method method1 = clazz.getMethod(GameUtils.mapName.md_get_redaol, new Class[0]);
            Method method2 = clazz.getMethod(GameUtils.mapName.md_redaol_getcomps, new Class[0]);
            lastRedaol = method2.invoke(method1.invoke(null, new Object[0]), new Object[0]).toString();
            if (lastRedaol.equals(EMPTY_BABA)) {
                return null;
            }
            Object mods = method2.invoke(method1.invoke(null, new Object[0]), new Object[0]);
            List modlist = (List)mods;
            ArrayList<ModEntity> modEntities = new ArrayList<ModEntity>();
            for (int idx = 0; idx < modlist.size(); ++idx) {
                Class<?> metaDataClass;
                Class<?> modClass = modlist.get(idx).getClass();
                if (modClass == null) {
                    Common.Log("modClass is null");
                    break;
                }
                ModEntity entity = new ModEntity();
                Method getSourceMethod = modClass.getMethod("getSource", new Class[0]);
                Method getModidMethod = modClass.getMethod("getModId", new Class[0]);
                Method getModnameMethod = modClass.getMethod("getName", new Class[0]);
                Method getModverMethod = modClass.getMethod("getVersion", new Class[0]);
                Method getModdataMethod = modClass.getMethod("getMetadata", new Class[0]);
                Method getModMethod = modClass.getMethod("getCustomModProperties", new Class[0]);
                File modSource = (File)getSourceMethod.invoke(modlist.get(idx), new Object[0]);
                if (modSource != null) {
                    entity.filename = modSource.getCanonicalPath();
                }
                if (modClass.getName().compareTo("net.minecraftforge.fml.common.InjectedModContainer") == 0) {
                    String jarFilePath;
                    Field wraperField = modClass.getDeclaredField("wrappedContainer");
                    Object wraper = wraperField.get(modlist.get(idx));
                    Class<?> wrapercls = wraper.getClass();
                    Common.Log(wrapercls.getName());
                    entity.filename = jarFilePath = wrapercls.getProtectionDomain().getCodeSource().getLocation().getPath();
                }
                entity.id = getModidMethod.invoke(modlist.get(idx), new Object[0]).toString();
                entity.name = getModnameMethod.invoke(modlist.get(idx), new Object[0]).toString();
                entity.ver = getModverMethod.invoke(modlist.get(idx), new Object[0]).toString();
                entity.md5 = getModMethod.invoke(modlist.get(idx), new Object[0]).toString();
                Object metaDataObj = getModdataMethod.invoke(modlist.get(idx), new Object[0]);
                if (metaDataObj != null && (metaDataClass = metaDataObj.getClass()) != null) {
                    Field descriptionField = metaDataClass.getDeclaredField("description");
                    Method authorListMethod = metaDataClass.getMethod("getAuthorList", new Class[0]);
                    entity.metadata = String.format("desc:%s|authors:%s", descriptionField.get(metaDataObj).toString(), authorListMethod.invoke(metaDataObj, new Object[0]).toString());
                }
                modEntities.add(entity);
            }
            Gson gson = new Gson();
            return gson.toJson(modEntities);
        }
        catch (Exception e) {
            Common.Log("Unable to getRedaolComps!");
            Common.CatchException(e);
            return null;
        }
    }

    public static void getRedaolComps(boolean check) {
        ModListMessageReply modListMessageReply = new ModListMessageReply();
        modListMessageReply.data = GameUtils.getRedaolCompsStr(check);
        NetworkHandler.send(4352, modListMessageReply.data);
        Common.Log("send ModListMessageReply:" + modListMessageReply.data);
    }

    public static void getMama() {
        ClientLog log = new ClientLog(GameState.uid, GameState.launchversion);
        Gson gson = new Gson();
        UrgentLog urgentLog = new UrgentLog();
        urgentLog.log = gson.toJson((Object)log);
        String requestJson = gson.toJson((Object)urgentLog);
        String result = HttpUtils.sendPost(HttpUtils.WEBSVR + "/urgent-log", requestJson);
        Common.Log("[mama]:" + result);
    }

    static {
        GameUtils.mapName.cn_redaol = "net.minecraftforge.fml.common.Loader";
        Common.Log("the version is " + Launch.version);
        if (Launch.version.compareTo("1.7.10") == 0) {
            GameUtils.mapName.cn_redaol = "cpw.mods.fml.common.Loader";
        }
        GameUtils.mapName.md_get_redaol = "instance";
        GameUtils.mapName.md_redaol_getcomps = "getActiveModList";
        GameUtils.mapName.cn_nativemc = "net.minecraft.client.Minecraft";
        GameUtils.mapName.md_get_nativemc = "func_71410_x";
        GameUtils.mapName.md_nativemc_getrpr = "func_110438_M";
        GameUtils.mapName.md_rpr_getkcaps = "func_110609_b";
    }
}

