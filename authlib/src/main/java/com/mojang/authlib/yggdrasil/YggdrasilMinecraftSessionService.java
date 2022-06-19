/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Iterables
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonParseException
 *  org.apache.commons.codec.Charsets
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.authlib.yggdrasil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.AuthenticationCpp;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilMinecraftSessionService
extends HttpMinecraftSessionService {
    private static final String[] WHITELISTED_DOMAINS = new String[]{".163.com", ".netease.com", ".minecraft.net", ".mojang.com"};
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean forClient = true;
    private static final boolean disabled = false;
    private static final boolean configurable = true;
    private static final AuthenticationCpp auth = new AuthenticationCpp();
    private static final AuthlibUrl authlibUrl = YggdrasilMinecraftSessionService.FindAuthlibUrl();
    private static final URL JOIN_URL_ = HttpAuthenticationService.constantURL(YggdrasilMinecraftSessionService.authlibUrl.AuthServerJoinUrl);
    private static final URL CHECK_URL_ = HttpAuthenticationService.constantURL(YggdrasilMinecraftSessionService.authlibUrl.AuthServerCheckUrl);
    private final PublicKey publicKey;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, (Object)new UUIDTypeAdapter()).create();
    private final LoadingCache<GameProfile, GameProfile> insecureProfiles = CacheBuilder.newBuilder().expireAfterWrite(6L, TimeUnit.HOURS).build((CacheLoader)new CacheLoader<GameProfile, GameProfile>(){

        public GameProfile load(GameProfile key) throws Exception {
            return YggdrasilMinecraftSessionService.this.fillGameProfile(key, false);
        }
    });

    protected YggdrasilMinecraftSessionService(YggdrasilAuthenticationService authenticationService) {
        super(authenticationService);
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray((InputStream)YggdrasilMinecraftSessionService.class.getResourceAsStream("/netease.der")));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(spec);
        }
        catch (Exception ignored) {
            throw new Error("Missing/invalid yggdrasil public key!");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String ReadFileString(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            String string = sb.toString();
            return string;
        }
        finally {
            br.close();
        }
    }

    private static String GetAuthlibJsonPath() {
        String authlibJsonPath = "authlib.json";
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);
        System.out.println("OS current temporary directory is " + tempDir);
        Path filePath = Paths.get(tempDir, authlibJsonPath);
        authlibJsonPath = filePath.toString();
        File authlibJson = new File(authlibJsonPath);
        if (authlibJson.exists()) {
            return authlibJsonPath;
        }
        authlibJsonPath = "authlib.json";
        authlibJson = new File(authlibJsonPath);
        return authlibJson.exists() ? authlibJsonPath : "";
    }

    private static AuthlibUrl FindAuthlibUrl() {
        try {
            String authlibJsonPath = YggdrasilMinecraftSessionService.GetAuthlibJsonPath();
            if (authlibJsonPath.isEmpty()) {
                LOGGER.info("Use the default value for authlib");
                return new AuthlibUrl();
            }
            Gson gson = new Gson();
            String urlString = YggdrasilMinecraftSessionService.ReadFileString(authlibJsonPath);
            AuthlibUrl authlibUrl = (AuthlibUrl)gson.fromJson(urlString, AuthlibUrl.class);
            File authlibJson = new File(authlibJsonPath);
            authlibJson.delete();
            return authlibUrl;
        }
        catch (Exception e) {
            LOGGER.error("FindAuthlibUrl Exception! Use the default value");
            return new AuthlibUrl();
        }
    }

    @Override
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        DataOutputStream out;
        DataInputStream input;
        Socket socket;
        String ret;
        try {
            socket = null;
            try {
                try {
                    socket = new Socket("127.0.0.1", 13337);
                    socket.setSoTimeout(25000);
                    input = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("CL7|Connect|" + serverId);
                    ret = input.readUTF();
                    System.out.println("服务器端返回过来的是: " + ret);
                    out.close();
                    input.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    if (socket == null)
                    try {
                        socket.close();
                    }
                    catch (IOException e3) {
                        socket = null;
                        System.out.println("客户端finally异常:" + e3.getMessage());
                    }
                }
            }
            finally {
                if (socket != null) {
                    try {
                        socket.close();
                    }
                    catch (IOException e) {
                        socket = null;
                        System.out.println("客户端finally异常:" + e.getMessage());
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        String name = user.getName();
        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
        GameProfile result = new GameProfile(uuid, name);
        return result;
    }

    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
        MinecraftTexturesPayload result;
        Property textureProperty = (Property)Iterables.getFirst((Iterable)profile.getProperties().get("textures"), null);
        if (textureProperty == null) {
            return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
        }
        if (requireSecure) {
            if (!textureProperty.hasSignature()) {
                LOGGER.error("Signature is missing from textures payload");
                throw new InsecureTextureException("Signature is missing from textures payload");
            }
            if (!textureProperty.isSignatureValid(this.publicKey)) {
                LOGGER.error("Textures payload has been tampered with (signature invalid)");
                throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
            }
        }
        try {
            String json = new String(Base64.decodeBase64((String)textureProperty.getValue()), Charsets.UTF_8);
            result = (MinecraftTexturesPayload)this.gson.fromJson(json, MinecraftTexturesPayload.class);
        }
        catch (JsonParseException e) {
            LOGGER.error("Could not decode textures payload", (Throwable)e);
            return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
        }
        if (result == null || result.getTextures() == null) {
            return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
        }
        for (Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : result.getTextures().entrySet()) {
            if (YggdrasilMinecraftSessionService.isWhitelistedDomain(entry.getValue().getUrl())) continue;
            LOGGER.error("Textures payload has been tampered with (non-whitelisted domain)");
            return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
        }
        return result.getTextures();
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
        if (profile.getId() == null) {
            return profile;
        }
        if (!requireSecure) {
            return (GameProfile)this.insecureProfiles.getUnchecked(profile);
        }
        return this.fillGameProfile(profile, true);
    }

    protected GameProfile fillGameProfile(GameProfile profile, boolean requireSecure) {
        return profile;
    }

    @Override
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService)super.getAuthenticationService();
    }

    private static boolean isWhitelistedDomain(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        }
        catch (URISyntaxException ignored) {
            throw new IllegalArgumentException("Invalid URL '" + url + "'");
        }
        String domain = uri.getHost();
        for (int i = 0; i < WHITELISTED_DOMAINS.length; ++i) {
            if (!domain.endsWith(WHITELISTED_DOMAINS[i])) continue;
            return true;
        }
        return false;
    }

    private static class AuthlibUrl {
        public String AuthServerJoinUrl = "https://x19authserver.nie.netease.com/mark";
        public String AuthServerCheckUrl = "http://x19authserver.nie.netease.com/check";
    }
}

