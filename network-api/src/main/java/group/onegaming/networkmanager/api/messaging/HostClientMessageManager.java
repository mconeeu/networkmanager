/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.api.messaging;

import group.onegaming.networkmanager.api.messaging.request.ClientMessageRequestListener;

public interface HostClientMessageManager {

    void registerClientMessageListener(String uri, ClientMessageRequestListener listener);

}
