/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.api.manager;

import eu.mcone.networkmanager.host.api.event.Event;
import eu.mcone.networkmanager.host.api.event.Listener;
import eu.mcone.networkmanager.host.api.module.NetworkModule;

public interface EventManager {

    void registerListener(Listener<? extends Event> listener);

    <E extends Event> void callEvent(NetworkModule module, E event);

}
