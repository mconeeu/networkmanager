/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.server;

import eu.mcone.networkmanager.api.network.client.handler.WebRequestHandler;
import eu.mcone.networkmanager.api.network.packet.Packet;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.UUID;

public interface ServerChannelPacketHandler {

    void registerPacket(Class<? extends Packet> clazz);

    void registerWebRequestHandler(String name, WebRequestHandler handler);

    void writeAndFlush(UUID channelUuid, Object object);

    Channel getChannel(UUID channelUuid);

    Collection<Channel> getChannels();

}
