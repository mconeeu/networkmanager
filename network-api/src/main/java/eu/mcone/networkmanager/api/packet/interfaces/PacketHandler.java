/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.packet.interfaces;

import eu.mcone.networkmanager.api.packet.Packet;
import io.netty.channel.ChannelHandlerContext;

public interface PacketHandler<T extends Packet> {

    void onPacketReceive(T packet, ChannelHandlerContext ctx);

}
