/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.manager;

import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.api.NetworkModule;
import eu.mcone.networkmanager.core.console.ConsoleColor;
import eu.mcone.networkmanager.core.console.Logger;
import eu.mcone.networkmanager.core.module.ModuleInfo;
import eu.mcone.networkmanager.file.ModuleFileReader;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleManager extends eu.mcone.networkmanager.api.manager.ModuleManager {

    private ModuleFileReader moduleFileReader;
    /* ModuleName, ModuleMainClass */
    private Map<String, ModuleInfo> modules = new HashMap<>();
    @Getter
    private Boolean Running;

    public ModuleManager() {

    }

    public void loadModules() {
        Logger.log("ModuleManager", ConsoleColor.GREEN + "Loading modules...");
        try {
            File[] modules = NetworkManager.HOME_DIR.listFiles();
            if (modules != null) {
                for (File module : modules) {
                    JarFile jarFile = new JarFile(module);
                    Enumeration<JarEntry> e = jarFile.entries();

                    URL[] urls = {new URL("jar:file:" + module.getPath() + "!/")};
                    URLClassLoader cl = URLClassLoader.newInstance(urls);

                    while (e.hasMoreElements()) {
                        JarEntry je = e.nextElement();
                        if (je.isDirectory()) {
                            if (je.getName().equals("module.yml")) {
                                this.moduleFileReader = new ModuleFileReader(cl.getResourceAsStream("module.yml"));
                            } else if (je.getName().endsWith(".class")) {
                                Class<? extends NetworkModule> moduleMain = Class.forName(this.moduleFileReader.getModuleInfo().getModuleClass(), true, cl).asSubclass(NetworkModule.class);
                                moduleMain.getMethod("onEnable").invoke(moduleMain.newInstance());
                                this.modules.put(this.moduleFileReader.getModuleInfo().getModuleName(), this.moduleFileReader.getModuleInfo());
                            } else {
                                continue;
                            }
                        }

                        // -6 because of .class
                        String className = je.getName().substring(0, je.getName().length() - 6);
                        className = className.replace('/', '.');
                        cl.loadClass(className);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            Logger.err("ModuleManager", e.getMessage());
        }
    }

    public void startModule(final String moduleName) {
        try {
            Logger.log("ModuleManager", ConsoleColor.GREEN + "Start module " + moduleName);
            File moduleFile = new File(NetworkManager.HOME_DIR + moduleName);
            JarFile jarFile = new JarFile(moduleFile);
            Enumeration<JarEntry> e = jarFile.entries();

            URL[] urls = {new URL("jar:file:" + moduleFile.getPath() + "!/")};
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory()) {
                    if (je.getName().equals("module.yml")) {
                        this.moduleFileReader = new ModuleFileReader(cl.getResourceAsStream("module.yml"));
                    } else if (je.getName().endsWith(".class")) {
                        Class<? extends NetworkModule> moduleMain = Class.forName(this.moduleFileReader.getModuleInfo().getModuleClass(), true, cl).asSubclass(NetworkModule.class);
                        moduleMain.getMethod("onEnable").invoke(moduleMain.newInstance());
                        this.modules.put(this.moduleFileReader.getModuleInfo().getModuleName(), this.moduleFileReader.getModuleInfo());
                    } else {
                        continue;
                    }
                }

                // -6 because of .class
                /*
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                cl.loadClass(className);
                */
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            Logger.err("ModuleManager", e.getMessage());
        }
    }

    public void stopModule(final String moduleName) {
        try {
            Logger.log("ModuleManager", ConsoleColor.GREEN + "Stop module " + moduleName + "...");
            if (modules.containsKey(moduleName)) {
                if (modules.get(moduleName).getRunning()) {
                    Class<? extends NetworkModule> moduleClass = Class.forName(modules.get(moduleName).getModuleClass()).asSubclass(NetworkModule.class);
                    moduleClass.getMethod("onDisable").invoke(moduleClass.newInstance());
                } else {
                    Logger.log("ModuleManager", ConsoleColor.RED + "The module with the module name " + moduleName + " has already been stopped");
                }
            } else {
                Logger.log("ModuleManager", ConsoleColor.RED + "There is no module with the module name" + moduleName);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
            Logger.err("ModuleManager", e.getMessage());
        }
    }

    public void stopModules() {
        try {
            Logger.log("ModuleManager", "Stop all working modules...");
            for (ModuleInfo modules : this.modules.values()) {
                if (modules.getRunning()) {
                    Class<? extends NetworkModule> moduleClass = Class.forName(modules.getModuleClass()).asSubclass(NetworkModule.class);
                    moduleClass.getMethod("onDisable").invoke(moduleClass.newInstance());
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | ClassNotFoundException e) {
            Logger.err("MoudleManager", e.getMessage());
        }
    }

    public Map<String, ModuleInfo> getModules() {
        return this.modules;
    }
}
