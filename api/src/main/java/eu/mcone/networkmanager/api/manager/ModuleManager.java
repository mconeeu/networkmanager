/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.manager;

import eu.mcone.networkmanager.core.module.ModuleInfo;

import java.util.Map;

public abstract class ModuleManager {

    public abstract void loadModules();

    public abstract void startModule(final String moduleName);

    public abstract void stopModule(final String moduleName);

    public abstract void stopModules();

    public abstract Map<String, ModuleInfo> getModules();
}
