/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.api.pipeline;

import group.onegaming.networkmanager.api.packet.Packet;
import group.onegaming.networkmanager.api.packet.interfaces.PacketResolver;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Log
@AllArgsConstructor
public class Encoder extends MessageToByteEncoder<Packet> {

    private final PacketResolver resolver;

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        int packetID = resolver.getPacketId(packet.getClass());

        if (packetID > -1) {
            out.writeInt(packetID);
            packet.write(out);
        }
    }

}
