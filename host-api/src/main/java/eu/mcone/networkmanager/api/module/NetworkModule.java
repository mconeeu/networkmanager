/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.module;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class NetworkModule {

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

}
