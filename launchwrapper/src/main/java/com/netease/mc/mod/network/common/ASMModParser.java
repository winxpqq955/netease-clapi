/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javassist.ClassPool
 *  javassist.CtClass
 *  javassist.CtConstructor
 */
package com.netease.mc.mod.network.common;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.IForgeModifier;
import com.netease.mc.mod.network.common.Library;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.ProtectionDomain;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

public class ASMModParser
implements IForgeModifier {
    @Override
    public Class<?> transformClass(String className, ProtectionDomain domain, ClassLoader loader, byte[] classBytes) {
        try {
            ClassPool pool = new ClassPool(true);
            CtClass ct = pool.getCtClass(className);
            CtConstructor m = ct.getConstructors()[0];
            m.insertBeforeBody("try {java.lang.Class threadClazz = Class.forName(\"com.netease.mc.mod.network.common.ASMModParser\");java.lang.reflect.Method method = threadClazz.getMethod(\"convertMod\", new Class[] {java.io.InputStream.class});$1 = (java.io.InputStream)method.invoke(null, new Object[]{$1});} catch (Exception e) {}");
            Class clazz = pool.toClass(ct, loader, domain);
            ct.detach();
            return clazz;
        }
        catch (Exception e) {
            Common.CatchException(e);
            return null;
        }
    }

    public static InputStream convertMod(InputStream input) throws IOException {
        byte[] data = ASMModParser.readClass(input, false);
        byte[] decrypt = Library.DecryptClass(data);
        return new ByteArrayInputStream(decrypt);
    }

    private static byte[] readClass(InputStream is, boolean close) throws IOException {
        if (is == null) {
            throw new IOException("Class not found");
        }
        try {
            byte[] b = new byte[is.available()];
            int len = 0;
            while (true) {
                int n;
                if ((n = is.read(b, len, b.length - len)) == -1) {
                    byte[] c;
                    if (len < b.length) {
                        c = new byte[len];
                        System.arraycopy(b, 0, c, 0, len);
                        b = c;
                    }
                    c = b;
                    return c;
                }
                if ((len += n) != b.length) continue;
                int last = is.read();
                if (last < 0) {
                    byte[] byArray = b;
                    return byArray;
                }
                byte[] c = new byte[b.length + 1000];
                System.arraycopy(b, 0, c, 0, len);
                c[len++] = (byte)last;
                b = c;
            }
        }
        finally {
            if (close) {
                is.close();
            }
        }
    }
}

