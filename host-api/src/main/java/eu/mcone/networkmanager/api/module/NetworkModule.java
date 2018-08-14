/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.module;

public abstract class NetworkModule {

    public NetworkModule() {}

    public abstract void onEnable();

    public abstract void onDisable();

}
