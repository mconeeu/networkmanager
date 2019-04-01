/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.api.server;

import eu.mcone.networkmanager.api.messaging.response.ClientMessageResponseListener;
import eu.mcone.networkmanager.api.packet.ClientMessageRequestPacket;
import eu.mcone.networkmanager.api.packet.Packet;
import eu.mcone.networkmanager.api.packet.interfaces.PacketHandler;
import eu.mcone.networkmanager.host.api.module.NetworkModule;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.Collection;

public interface ServerPacketManager {

    void registerConnectionListener(ConnectionListener listener);

    <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz, PacketHandler<T> handler);

    <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz);

    <T extends Packet> void registerAdditionalPacketHandler(Class<T> clazz, PacketHandler<T> handler) throws IllegalStateException;

    Collection<Channel> getClients();

    Channel getClient(String resourceBundleName);

    ChannelFuture send(Channel channel, Packet packet);

    ChannelFuture sendClientRequest(Channel channel, ClientMessageRequestPacket packet, ClientMessageResponseListener callback);

}
