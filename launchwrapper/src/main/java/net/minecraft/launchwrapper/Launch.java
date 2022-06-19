/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 */
package net.minecraft.launchwrapper;

import com.netease.mc.mod.network.common.ASMModParser;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.common.Library;
import com.netease.mc.mod.network.common.NeteaseMain;
import com.netease.mc.mod.network.common.UserPropertiesEx;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.*;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.launchwrapper.LogWrapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class Launch {
    private static final String DEFAULT_TWEAK = "net.minecraft.launchwrapper.VanillaTweaker";
    public static File minecraftHome;
    public static File assetsDir;
    public static Map<String, Object> blackboard;
    public static String version;
    public static String[] neteaseExclude;
    public static LaunchClassLoader classLoader;
    public static ClassLoader mainLoader;

    public static void main(String[] args) {
        new Launch().launch(args);
    }

    private Launch() {
        URLClassLoader ucl = (URLClassLoader)this.getClass().getClassLoader();
        classLoader = new LaunchClassLoader(ucl.getURLs());
        blackboard = new HashMap<String, Object>();
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    private void launch(String[] args) {
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            Class<?> VirtualMachineCls = Class.forName("com.sun.tools.attach.VirtualMachine");
            Class[] argsClass = new Class[]{pid.getClass()};
            VirtualMachineCls.getMethod("attach", String.class).invoke(null, pid);
            LogManager.getLogger().info("There are something wrong! JVM can attach!");
            return;
        }
        catch (Exception name) {
            String profileName;
            try {
                Class<?> t = Class.forName(GameState.class.getName(), true, classLoader);
                Class[] argsClass = new Class[]{args.getClass()};
                t.getMethod("initGameType", argsClass).invoke(null, new Object[]{args});
            }
            catch (Exception e) {
                Common.Log("initGameType init failed!");
                Common.CatchException(e);
                return;
            }
            OptionParser parser = new OptionParser();
            parser.allowsUnrecognizedOptions();
            ArgumentAcceptingOptionSpec profileOption = parser.accepts("version", "The version we launched with").withRequiredArg();
            ArgumentAcceptingOptionSpec gameDirOption = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().ofType(File.class);
            ArgumentAcceptingOptionSpec assetsDirOption = parser.accepts("assetsDir", "Assets directory").withRequiredArg().ofType(File.class);
            final OptionSpec<String> tweakClassOption = parser.accepts("tweakClass", "Tweak class(es) to load").withRequiredArg().defaultsTo(DEFAULT_TWEAK);
            NonOptionArgumentSpec nonOption = parser.nonOptions();
            OptionSet options = parser.parse(args);
            minecraftHome = (File)options.valueOf((OptionSpec)gameDirOption);
            assetsDir = (File)options.valueOf((OptionSpec)assetsDirOption);
            version = profileName = (String)options.valueOf((OptionSpec)profileOption);
            Library.PreInit();
            classLoader.registerForgeModifier("net.minecraftforge.fml.common.discovery.asm.ASMModParser", new ASMModParser());
            classLoader.registerForgeModifier("cpw.mods.fml.common.discovery.asm.ASMModParser", new ASMModParser());
            ArrayList tweakClassNames = new ArrayList(options.valuesOf((OptionSpec)tweakClassOption));
            ArrayList<String> argumentList = new ArrayList<String>();
            blackboard.put("TweakClasses", tweakClassNames);
            blackboard.put("ArgumentList", argumentList);
            HashSet<String> allTweakerNames = new HashSet<String>();
            ArrayList<ITweaker> allTweakers = new ArrayList<ITweaker>();
            try {
                ArrayList<ITweaker> tweakers = new ArrayList<ITweaker>(tweakClassNames.size() + 1);
                blackboard.put("Tweaks", tweakers);
                ITweaker primaryTweaker = null;
                do {
                    for (final Iterator<String> it = tweakClassNames.iterator(); it.hasNext(); ) {
                        final String tweakName = it.next();
                        // Safety check - don't reprocess something we've already visited
                        if (allTweakerNames.contains(tweakName)) {
                            LogWrapper.log(Level.WARN, "Tweak class name %s has already been visited -- skipping", tweakName);
                            // remove the tweaker from the stack otherwise it will create an infinite loop
                            it.remove();
                            continue;
                        } else {
                            allTweakerNames.add(tweakName);
                        }
                        LogWrapper.log(Level.INFO, "Loading tweak class name %s", tweakName);

                        // Ensure we allow the tweak class to load with the parent classloader
                        classLoader.addClassLoaderExclusion(tweakName.substring(0,tweakName.lastIndexOf('.')));
                        final ITweaker tweaker = (ITweaker) Class.forName(tweakName, true, classLoader).newInstance();
                        tweakers.add(tweaker);

                        // Remove the tweaker from the list of tweaker names we've processed this pass
                        it.remove();
                        // If we haven't visited a tweaker yet, the first will become the 'primary' tweaker
                        if (primaryTweaker == null) {
                            LogWrapper.log(Level.INFO, "Using primary tweak class name %s", tweakName);
                            primaryTweaker = tweaker;
                        }
                    }

                    // Now, iterate all the tweakers we just instantiated
                    for (final Iterator<ITweaker> it = tweakers.iterator(); it.hasNext(); ) {
                        final ITweaker tweaker = it.next();
                        LogWrapper.log(Level.INFO, "Calling tweak class %s", tweaker.getClass().getName());
                        tweaker.acceptOptions(options.valuesOf(nonOption), minecraftHome, assetsDir, profileName);
                        tweaker.injectIntoClassLoader(classLoader);
                        allTweakers.add(tweaker);
                        // again, remove from the list once we've processed it, so we don't get duplicates
                        it.remove();
                    }
                } while (!tweakClassNames.isEmpty());

                // Once we're done, we then ask all the tweakers for their arguments and add them all to the
                // master argument list
                for (final ITweaker tweaker : allTweakers) {
                    argumentList.addAll(Arrays.asList(tweaker.getLaunchArguments()));
                }
                for (String className : neteaseExclude) {
                    classLoader.addClassLoaderExclusion(className);
                }
                Library.Test();
                try {
                    Class<?> t = Class.forName(NeteaseMain.class.getName(), true, classLoader);
                    Class[] argsClass = new Class[]{args.getClass()};
                    t.getMethod("init", argsClass).invoke(null, new Object[]{args});
                }
                catch (Exception e) {
                    Common.Log("NeteaseMain init failed!");
                    Common.CatchException(e);
                    return;
                }
                String launchTarget = primaryTweaker.getLaunchTarget();
                Class<?> clazz = Class.forName(launchTarget, false, classLoader);
                Method mainMethod = clazz.getMethod("main", String[].class);
                LogWrapper.info("Launching wrapped minecraft {%s}", launchTarget);
                mainMethod.invoke(null, new Object[]{argumentList.toArray(new String[argumentList.size()])});
                Library.Close();
            }
            catch (Exception e) {
                LogWrapper.log(Level.ERROR, e, "Unable to launch", new Object[0]);
                Library.Close();
                System.exit(1);
            }
            return;
        }
    }

    static {
        neteaseExclude = new String[]{Library.class.getName(), "com.mojang.authlib.AuthenticationCpp", GameState.class.getName(), UserPropertiesEx.class.getName()};
        mainLoader = Launch.class.getClassLoader();
        try {
            Library.LoadLibrary();
        }
        catch (Exception e) {
            Common.CatchException(e);
        }
    }
}

