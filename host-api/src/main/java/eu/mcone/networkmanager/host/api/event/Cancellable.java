/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.api.event;

public interface Cancellable {

    void setCancelled(boolean cancel);

    boolean isCancelled();

}
