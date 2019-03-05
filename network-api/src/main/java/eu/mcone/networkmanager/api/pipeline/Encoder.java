/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.pipeline;

import eu.mcone.networkmanager.api.packet.Packet;
import eu.mcone.networkmanager.api.packet.interfaces.PacketResolver;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Log
@AllArgsConstructor
public class Encoder extends MessageToByteEncoder<Packet> {

    private PacketResolver resolver;

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        int packetID = resolver.getPacketId(packet.getClass());
        log.finest("Encoding packet "+packet.getClass().getSimpleName()+" to "+ctx.channel().remoteAddress());

        if (packetID > -1) {
            out.writeInt(packetID);
            packet.write(out);
        }
    }

}
