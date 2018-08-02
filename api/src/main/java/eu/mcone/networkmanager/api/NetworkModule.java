/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api;

import eu.mcone.networkmanager.api.module.ModuleInfo;
import lombok.Getter;

public abstract class NetworkModule {

    @Getter
    private ModuleInfo info;

    public NetworkModule() {}

    public NetworkModule(ModuleInfo info) {
        this.info = info;
    }

    public abstract void onEnable();

    public abstract void onDisable();

}
