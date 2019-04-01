/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.api.manager;

import eu.mcone.networkmanager.host.api.module.NetworkModule;
import eu.mcone.networkmanager.host.api.module.ModuleInfo;

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
