/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.manager;

import eu.mcone.networkmanager.api.event.Event;
import eu.mcone.networkmanager.api.event.Listener;
import eu.mcone.networkmanager.api.module.NetworkModule;

public interface EventManager {

    void registerListener(Listener<? extends Event> listener);

    <E extends Event> void callEvent(NetworkModule module, E event);

}
