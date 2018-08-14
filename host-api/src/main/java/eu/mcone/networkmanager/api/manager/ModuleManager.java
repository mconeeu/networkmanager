/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.manager;

import eu.mcone.networkmanager.api.module.ModuleInfo;
import eu.mcone.networkmanager.api.module.NetworkModule;

public interface ModuleManager {

    void reload();

    void close();

    void enableModule(ModuleInfo info);

    void disableModule(ModuleInfo info);

    void disableModules();

    void reloadModule(ModuleInfo info);

    ModuleInfo getModuleInfo(String module);

    NetworkModule getModule(ModuleInfo info);

    NetworkModule getModule(String module);

}
