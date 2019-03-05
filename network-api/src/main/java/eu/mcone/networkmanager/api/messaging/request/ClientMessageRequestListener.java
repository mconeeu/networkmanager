/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.messaging.request;

import eu.mcone.networkmanager.api.packet.ClientMessageRequestPacket;
import eu.mcone.networkmanager.api.packet.ClientMessageResponsePacket;

public interface ClientMessageRequestListener {

    ClientMessageResponsePacket onClientRequest(ClientMessageRequestPacket packet);

}
