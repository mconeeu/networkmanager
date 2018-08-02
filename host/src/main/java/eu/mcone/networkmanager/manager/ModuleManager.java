/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.manager;

import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.api.NetworkModule;
import eu.mcone.networkmanager.console.ConsoleColor;
import eu.mcone.networkmanager.console.Logger;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleManager implements eu.mcone.networkmanager.api.manager.ModuleManager {

    @Getter
    private List<NetworkModule> modules;

    public ModuleManager() {
        modules = new ArrayList<>();
        loadModules();
    }

    private void loadModules() {
        File[] modules = NetworkManager.HOME_DIR.listFiles();

        if (modules != null) {
            for (File module : modules) {
                if (module.getName().endsWith(".jar")) {
                    loadModule(module);
                }
            }
        }
    }

    @Override
    public void loadModule(final File module) {
        try {
            Logger.log("ModuleManager", ConsoleColor.GREEN + "Loaded module " + module.getName());
            JarFile jarFile = new JarFile(module);
            Enumeration<JarEntry> e = jarFile.entries();

            URLClassLoader cl = new URLClassLoader(new URL[]{module.toURI().toURL()});
            Class<? extends NetworkModule> mainClass = null;

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (!je.isDirectory()) {
                    if (je.getName().endsWith(".class")) {
                        String className = je.getName().substring(0, je.getName().length() - 6);
                        className = className.replace('/', '.');
                        Class<?> loaded = cl.loadClass(className);

                        try {
                            mainClass = loaded.asSubclass(NetworkModule.class);
                        } catch (ClassCastException ignored) {
                        }
                    }
                }
            }

            cl.close();
            if (mainClass != null) {
                NetworkModule moduleObject = mainClass.newInstance();
                moduleObject.getInfo().setFile(module);
                enableModule(moduleObject);
                this.modules.add(moduleObject);
            } else {
                throw new UnsupportedOperationException("Module does not contain Class extending NetworkModule. Aborting...");
            }
        } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void enableModule(final NetworkModule module) {
        Logger.log("ModuleManager", ConsoleColor.GREEN + "Enabled module " + module.getInfo().getModuleName());
        module.getInfo().setRunning(true);
        module.onEnable();
    }

    @Override
    public void disableModule(final NetworkModule module) {
        Logger.log("ModuleManager", ConsoleColor.GREEN + "Stop module " + module.getInfo().getModuleName() + "...");
        if (modules.contains(module)) {
            if (module.getInfo().isRunning()) {
                module.onDisable();
                module.getInfo().setRunning(false);
            } else {
                Logger.log("ModuleManager", ConsoleColor.RED + "The module with the module name " + module.getInfo().getModuleName() + " has already been stopped");
            }
        } else {
            Logger.log("ModuleManager", ConsoleColor.RED + "There is no module with the module name" + module.getInfo().getModuleName());
        }
    }

    public void reloadModule(final NetworkModule module) {
        Logger.log(getClass(), ConsoleColor.GREEN + "Reload module " + module.getInfo().getModuleName());
        if (module.getInfo().isRunning()) {
            module.onDisable();
        }
        module.onEnable();
    }

    public void reloadModules() {
        for (NetworkModule networkModule : modules) {
            reloadModule(networkModule);
        }
    }

    public void disableModules() {
        Logger.log("ModuleManager", "Stop all working modules...");
        for (NetworkModule module : this.modules) {
            disableModule(module);
        }
    }

    public NetworkModule getModule(final String moduleName) {
        for (NetworkModule networkModule : modules) {
            if (networkModule.getInfo().getModuleName().equalsIgnoreCase(moduleName)) {
                return networkModule;
            }
        }
        return null;
    }
}
