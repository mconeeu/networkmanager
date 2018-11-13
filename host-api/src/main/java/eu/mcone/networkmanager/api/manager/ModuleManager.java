/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.manager;

import eu.mcone.networkmanager.api.module.ModuleInfo;
import eu.mcone.networkmanager.api.module.NetworkModule;

import java.util.Set;

public interface ModuleManager {

    Set<NetworkModule> getLoadedModules();

    Set<ModuleInfo> getUnloadedModules();


    void loadModules();

    void loadModule(String name);


    void enableLoadedModules();

    void enableLoadedModule(String name);


    void disableLoadedModules();

    void disableLoadedModule(String name);


    void reloadLoadedModules();

    void reloadLoadedModule(String name);

}
