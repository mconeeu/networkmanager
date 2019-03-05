/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.messaging;

import eu.mcone.networkmanager.api.messaging.request.ClientMessageRequestListener;

public interface HostClientMessageManager {

    void registerClientMessageListener(String uri, ClientMessageRequestListener listener);

}
