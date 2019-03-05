/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.messaging.request;

import eu.mcone.networkmanager.api.packet.ClientMessageRequestPacket;
import eu.mcone.networkmanager.api.packet.ClientMessageResponsePacket;

public abstract class CustomClientMessageRequestListener<P extends ClientMessageRequestPacket> implements ClientMessageRequestListener {

    @SuppressWarnings("unchecked")
    @Override
    public ClientMessageResponsePacket onClientRequest(ClientMessageRequestPacket packet) {
        try {
            return onCustomClientRequest((P) packet);
        } catch (ClassCastException e) {
            throw new IllegalStateException("ClientMessageRequestPacket could not be casted to CustomPacketType", e);
        }
    }

    protected abstract ClientMessageResponsePacket onCustomClientRequest(P packet);

}
