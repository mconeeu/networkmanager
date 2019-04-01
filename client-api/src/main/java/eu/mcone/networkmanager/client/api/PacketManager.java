/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.client.api;

import eu.mcone.networkmanager.api.messaging.response.ClientMessageResponseListener;
import eu.mcone.networkmanager.api.packet.ClientMessageRequestPacket;
import eu.mcone.networkmanager.api.packet.Packet;
import eu.mcone.networkmanager.api.packet.interfaces.PacketHandler;
import io.netty.channel.ChannelFuture;

public interface PacketManager {

    <T extends Packet> void registerPacketHandler(Class<T> clazz, PacketHandler<T> handler);

    ChannelFuture send(Packet packet);

    ChannelFuture sendClientRequest(ClientMessageRequestPacket packet, ClientMessageResponseListener callback);

}
