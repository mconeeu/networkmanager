/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.manager;

import eu.mcone.networkmanager.api.NetworkModule;

import java.io.File;
import java.util.List;

public interface ModuleManager {

    void loadModule(File module);

    void enableModule(NetworkModule module);

    void disableModule(NetworkModule module);

    void reloadModule(NetworkModule module);

    void reloadModules();

    NetworkModule getModule(String module);

    List<NetworkModule> getModules();

}
