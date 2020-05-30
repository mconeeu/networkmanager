/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.api.messaging.response;

import group.onegaming.networkmanager.api.packet.ClientMessageResponsePacket;

public interface ClientMessageResponseListener {

    void onClientRequest(ClientMessageResponsePacket packet);

}
