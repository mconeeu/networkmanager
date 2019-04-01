/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.messaging.response;

import eu.mcone.networkmanager.api.packet.ClientMessageResponsePacket;

public abstract class CustomClientMessageResponseListener<P extends ClientMessageResponsePacket> implements ClientMessageResponseListener {

    @SuppressWarnings("unchecked")
    @Override
    public void onClientRequest(ClientMessageResponsePacket packet) {
        try {
            onCustomClientRequest((P) packet);
        } catch (ClassCastException e) {
            throw new IllegalStateException("ClientMessageRequestPacket could not be casted to CustomPacketType", e);
        }
    }

    protected abstract ClientMessageResponsePacket onCustomClientRequest(P packet);

}
