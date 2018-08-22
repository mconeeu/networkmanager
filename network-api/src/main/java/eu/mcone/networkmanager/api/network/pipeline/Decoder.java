/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.pipeline;

import eu.mcone.networkmanager.api.network.packet.Packet;
import eu.mcone.networkmanager.api.network.packet.PacketResolver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Decoder extends ByteToMessageDecoder {

    private PacketResolver resolver;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in instanceof EmptyByteBuf) return;
        Class<? extends Packet> packetClass = resolver.getPacketById(in.readInt());

        if (packetClass != null) {
            Packet packet = packetClass.newInstance();
            packet.read(in);

            out.add(packet);
        }
    }

}
