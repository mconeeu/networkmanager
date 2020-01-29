/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.pipeline;

import eu.mcone.networkmanager.api.packet.Packet;
import eu.mcone.networkmanager.api.packet.interfaces.PacketResolver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import java.util.List;

@Log
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
