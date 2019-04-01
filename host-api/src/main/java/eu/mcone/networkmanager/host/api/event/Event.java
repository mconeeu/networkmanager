/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.api.event;

import eu.mcone.networkmanager.host.api.module.NetworkModule;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public abstract class Event {

    @Getter
    private NetworkModule module;

    public void setModule(NetworkModule module) {
        if (module == null) {
            log.severe("BungeeCoreSystem instance cannot be set twice!");
        } else {
            this.module = module;
        }
    }

}
