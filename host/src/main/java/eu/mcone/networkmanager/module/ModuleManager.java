/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.module;

import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.api.module.ModuleInfo;
import eu.mcone.networkmanager.api.module.NetworkModule;
import eu.mcone.networkmanager.core.api.console.ConsoleColor;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Log
public class ModuleManager implements eu.mcone.networkmanager.api.manager.ModuleManager {

    private final static File MODULE_DIR = new File(NetworkManager.HOME_DIR, "modules");
    private Set<ModuleInfo> modules;
    private Set<NetworkModule> loaded;

    public ModuleManager() {
        modules = new HashSet<>();
        loaded = new HashSet<>();
        detectModules();
    }

    private void detectModules() {
        log.info("Detecting modules...");

        if (!MODULE_DIR.exists()) {
            MODULE_DIR.mkdir();
        }

        File[] modules = MODULE_DIR.listFiles();

        if (modules != null) {
            fileLoop:
            for (File module : modules) {
                if (module.isFile() && module.getName().endsWith(".jar")) {
                    try (JarFile jar = new JarFile(module)) {
                        JarEntry moduleInfoJson = jar.getJarEntry("module.json");

                        if (moduleInfoJson != null) {
                            try (InputStream in = jar.getInputStream(moduleInfoJson)) {
                                ModuleInfo moduleInfo = NetworkManager.getManager().getGson().fromJson(new InputStreamReader(in), ModuleInfo.class);

                                if (moduleInfo.getMain() != null && moduleInfo.getName() != null) {
                                    //checking if module is already detected
                                    for (ModuleInfo info : this.modules) {
                                        if (info.getName().equals(moduleInfo.getName())) {
                                            continue fileLoop;
                                        }
                                    }

                                    moduleInfo.setFile(module);
                                    this.modules.add(moduleInfo);
                                } else {
                                    throw new NullPointerException(module.getName() + " has invalid module.json! Aborting...");
                                }
                            }
                        } else {
                            throw new NullPointerException(module.getName() + " does not contain a module.json File! Aborting...");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        log.severe(e.getMessage());
                    }
                }
            }
        }
    }




    @Override
    public void loadModules() {
        log.info("Loading detected modules...");
        for (ModuleInfo info : modules) {
            loadModule(info);
        }
    }

    public void loadModule(String name) {
        ModuleInfo info = getDetectedModule(name);

        if (info != null) {
            loadModule(info);
        } else {
            log.severe("Module was not detected!");
        }
    }

    private void loadModule(ModuleInfo info) {
        log.info(ConsoleColor.GREEN + "Loading module " + info.getName());

        if (!isLoaded(info)) {
            try {
                URLClassLoader loader = new URLClassLoader(
                        new URL[]{
                                info.getFile().toURI().toURL()
                        }
                );
                Class<?> main = loader.loadClass(info.getMain());
                NetworkModule module = (NetworkModule) main.getDeclaredConstructor().newInstance();

                module.setModuleInfo(info);
                loaded.add(module);

                module.onLoad();
                log.fine("Loaded Module " + info.getName());
            } catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                log.severe("Error while loading Module " + info.getName() + ":");
                e.printStackTrace();
            }
        } else {
            log.severe("Module is still loaded!");
        }
    }




    @Override
    public void enableLoadedModules() {
        log.info("Enabling all loaded Modules...");
        for (NetworkModule module : loaded) {
            enableModule(module);
        }
    }

    @Override
    public void enableLoadedModule(String name) {
        NetworkModule module = getLoadedModule(name);

        if (module != null) {
            enableModule(module);
        } else {
            log.severe("Module is not loaded!");
        }
    }

    private void enableModule(NetworkModule module) {
        module.onEnable();
    }




    @Override
    public void disableLoadedModules() {
        log.info("Stopping all running modules...");
        for (NetworkModule module : loaded) {
            disableModule(module);
        }
    }

    @Override
    public void disableLoadedModule(String name) {
        NetworkModule module = getLoadedModule(name);

        if (module != null) {
            log.info(ConsoleColor.GREEN + "Stopping module " + name + "...");
            disableModule(module);
        } else {
            log.severe(ConsoleColor.YELLOW + "Module " + name + " is not loaded or does not exist!");
        }
    }

    private void disableModule(NetworkModule module) {
        module.onDisable();
        NetworkManager.getManager().getChannelPacketHandler().unregisterPackets(module);
        loaded.remove(module);
    }




    @Override
    public void reloadLoadedModules() {
        log.info("Reloading all loaded modules...");

        disableLoadedModules();
        modules.clear();
        loaded.clear();

        detectModules();
        loadModules();
        enableLoadedModules();
    }

    @Override
    public void reloadLoadedModule(String name) {
        NetworkModule module = getLoadedModule(name);

        if (module != null) {
            reloadLoadedModule(module);
        } else {
            log.severe(ConsoleColor.YELLOW + "Module " + name + " is not loaded or does not exist!");
        }
    }

    private void reloadLoadedModule(NetworkModule module) {
        log.info(ConsoleColor.GREEN + "Reloading module " + module.getModuleInfo().getName());

        disableModule(module);
        module.onLoad();
        module.onEnable();
    }


    @Override
    public Set<NetworkModule> getLoadedModules() {
        return loaded;
    }

    @Override
    public Set<ModuleInfo> getUnloadedModules() {
        Set<ModuleInfo> result = new HashSet<>();

        for (ModuleInfo info : modules) {
            if (!isLoaded(info)) result.add(info);
        }

        return result;
    }

    private boolean isLoaded(ModuleInfo info) {
        for (NetworkModule module : loaded) {
            if (module.getModuleInfo().getName().equals(info.getName())) return true;
        }
        return false;
    }

    private NetworkModule getLoadedModule(String moduleName) {
        for (NetworkModule module : loaded) {
            if (module.getModuleInfo().getName().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    private ModuleInfo getDetectedModule(String moduleName) {
        for (ModuleInfo info : modules) {
            if (info.getName().equals(moduleName)) {
                return info;
            }
        }
        return null;
    }

}
