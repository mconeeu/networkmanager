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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Log
public class ModuleManager implements eu.mcone.networkmanager.api.manager.ModuleManager {

    private final static File MODULE_DIR = new File(NetworkManager.HOME_DIR, "modules");
    private Map<ModuleInfo, NetworkModule> modules;

    public ModuleManager() {
        modules = new HashMap<>();
        detectModules();
    }

    @Override
    public void reload() {
        reloadModules();
        detectModules();

        for (HashMap.Entry<ModuleInfo, NetworkModule> entry : modules.entrySet()) {
            if (entry.getValue() == null) {
                loadModule(entry.getKey());
            }
        }
    }

    @Override
    public void close() {
        disableModules();
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
                                    for (ModuleInfo info : this.modules.keySet()) {
                                        if (info.getName().equals(moduleInfo.getName())) {
                                            continue fileLoop;
                                        }
                                    }

                                    moduleInfo.setFile(module);
                                    this.modules.put(moduleInfo, null);
                                } else {
                                    throw new NullPointerException(module.getName() + " has invalid module.json! Aborting...");
                                }
                            }
                        } else {
                            throw new NullPointerException(module.getName()+ " does not contain a module.json File! Aborting...");
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
        log.info("Loading all non-running Modules...");
        for (ModuleInfo info : modules.keySet()) {
            if (!info.isRunning()) {
                loadModule(info);
            } else {
                log.warning("Cannot load Module "+info.getName()+". Its already running!");
            }
        }
    }

    @Override
    public void loadModule(final ModuleInfo info) {
        log.info(ConsoleColor.GREEN + "Loading module " + info.getName());

        if (!info.isRunning()) {
            try {
                URLClassLoader loader = new URLClassLoader(
                        new URL[]{
                                info.getFile().toURI().toURL()
                        }
                );
                Class<?> main = loader.loadClass(info.getMain());
                NetworkModule module = (NetworkModule) main.getDeclaredConstructor().newInstance();

                modules.put(info, module);

                info.setRunning(true);
                module.onLoad();
            } catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                log.severe("Error while loading Module "+info.getName()+":");
                e.printStackTrace();
            }
        } else {
            log.severe("Module is still loaded!");
        }
    }

    @Override
    public void enableModules() {
        log.info("Enabling all running Modules...");
        for (ModuleInfo info : modules.keySet()) {
            if (info.isRunning()) {
                enableModule(info);
            } else {
                log.warning("Cannot load Module "+info.getName()+". Its already running!");
            }
        }
    }

    @Override
    public void enableModule(ModuleInfo info) {
        if (!info.isRunning()) {
            modules.get(info).onEnable();
        } else {
            log.severe("Module is still loaded!");
        }
    }

    @Override
    public void disableModule(final ModuleInfo info) {
        log.info(ConsoleColor.GREEN + "Stopping module " + info.getName() + "...");

        if (modules.containsKey(info)) {
            if (info.isRunning()) {
                modules.get(info).onDisable();
                info.setRunning(false);

                modules.put(info, null);
            } else {
                log.warning(ConsoleColor.YELLOW + "The module with the module name " + info.getName() + " has already been stopped");
            }
        } else {
            log.severe(ConsoleColor.YELLOW + "There is no module with the module name" + info.getName());
        }
    }

    @Override
    public void reloadModule(final ModuleInfo info) {
        log.info(ConsoleColor.GREEN + "Reloading module " + info.getName());

        if (modules.containsKey(info)) {
            if (info.isRunning()) {
                disableModule(info);
                loadModule(info);
                enableModule(info);
            } else {
                log.warning("The module " + info.getName() + " cannot be reloaded. Its not running! Starting anyways...");
                loadModule(info);
                enableModule(info);
            }
        } else {
            log.severe(ConsoleColor.YELLOW + "There is no module with the module name" + info.getName());
        }
    }

    private void reloadModules() {
        log.info("Reloading all running modules...");

        for (ModuleInfo info : modules.keySet()) {
            if (info.isRunning()) {
                reloadModule(info);
            }
        }
    }

    @Override
    public void disableModules() {
        log.info("Stopping all running modules...");

        for (ModuleInfo info : modules.keySet()) {
            if (info.isRunning()) {
                disableModule(info);
            }
        }
    }

    public ModuleInfo getModuleInfo(final String moduleName) {
        for (ModuleInfo info : modules.keySet()) {
            if (info.getName().equalsIgnoreCase(moduleName)) {
                return info;
            }
        }
        return null;
    }

    public NetworkModule getModule(final String moduleName) {
        return modules.getOrDefault(getModuleInfo(moduleName), null);
    }

    public NetworkModule getModule(final ModuleInfo info) {
        return modules.getOrDefault(info, null);
    }

    public Collection<ModuleInfo> getModules() {
        return modules.keySet();
    }

}
