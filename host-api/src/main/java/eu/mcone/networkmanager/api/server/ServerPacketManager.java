/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.server;

import eu.mcone.networkmanager.api.module.NetworkModule;
import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import eu.mcone.networkmanager.api.network.client.handler.WebRequestHandler;
import eu.mcone.networkmanager.api.network.packet.Packet;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.UUID;

public interface ServerPacketManager {

    void registerConnectionListener(ConnectionListener listener);

    <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz, PacketHandler<T> handler);

    <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz);

    <T extends Packet> void registerAdditionalPacketHandler(Class<T> clazz, PacketHandler<T> handler) throws IllegalStateException;

    void registerWebRequestHandler(String name, WebRequestHandler handler);

    void writeAndFlush(UUID channelUuid, Object object);

    Channel getChannel(UUID channelUuid);

    Collection<Channel> getChannels();

}
