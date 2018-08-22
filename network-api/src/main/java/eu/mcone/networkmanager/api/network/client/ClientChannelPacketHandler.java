/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.client;

import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import eu.mcone.networkmanager.api.network.packet.ClientRegisterPacketHost;
import eu.mcone.networkmanager.api.network.packet.Packet;
import eu.mcone.networkmanager.api.network.packet.PacketRegisterPacketClient;
import eu.mcone.networkmanager.api.network.packet.PacketResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log
public class ClientChannelPacketHandler extends SimpleChannelInboundHandler<Packet> implements PacketResolver {

    private final ClientBootstrap bootstrap;
    private Map<Class<? extends Packet>, Integer> packetIds;

    ClientChannelPacketHandler(ClientBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.packetIds = new HashMap<>();

        packetIds.put(ClientRegisterPacketHost.class, 0);
        packetIds.put(PacketRegisterPacketClient.class, 1);

        PacketRegisterPacketClient.addHandler((PacketHandler<PacketRegisterPacketClient>) (packet, chc) -> {
            for (HashMap.Entry<Class<? extends Packet>, Integer> entry : packet.getRegisteredPackets().entrySet()) {
                packetIds.put(entry.getKey(), entry.getValue());
            }

            log.info("Successfully got all Packet-IDs from host. Calling onChannelActiveMethod");
            bootstrap.client.onChannelActive(chc);
        });
    }

    @Override
    public Class<? extends Packet> getPacketById(int id) {
        for (Map.Entry<Class<? extends Packet>, Integer> entry : packetIds.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public int getPacketId(Class<? extends Packet> packet) {
        return packetIds.getOrDefault(packet, null);
    }

    @Override
    public void channelActive(ChannelHandlerContext chc) {
        chc.writeAndFlush(new ClientRegisterPacketHost(bootstrap.getResourceBundleName()));
        log.info("new channel to " + chc.channel().remoteAddress().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chc, Packet packet) {
        log.info("received "+packet.getClass().getSimpleName()+" from " + chc.channel().remoteAddress().toString());

        for (PacketHandler handler : packet.getHandlerList()) {
            handler.onPacketReceive(packet, chc);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext chc) {
        bootstrap.client.onChannelUnregistered(chc);
        bootstrap.scheduleReconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.severe(cause.getMessage());
            log.severe("Reconnecting...");
            return;
        }

        log.severe("Netty Exception:");
        cause.printStackTrace();
    }
}
