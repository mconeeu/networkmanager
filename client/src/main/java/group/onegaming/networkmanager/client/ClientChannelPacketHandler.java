/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.client;

import group.onegaming.networkmanager.api.messaging.ClientMessageManager;
import group.onegaming.networkmanager.api.packet.ClientMessageRequestPacket;
import group.onegaming.networkmanager.api.packet.ClientMessageResponsePacket;
import group.onegaming.networkmanager.api.packet.ClientRegisterPacketHost;
import group.onegaming.networkmanager.api.packet.Packet;
import group.onegaming.networkmanager.api.packet.interfaces.PacketHandler;
import group.onegaming.networkmanager.api.packet.interfaces.PacketResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Set;

@Log
public class ClientChannelPacketHandler extends SimpleChannelInboundHandler<Packet> implements PacketResolver {

    private final ClientPacketManager packetManager;
    @Getter
    private final ClientMessageManager messageManager;

    public ClientChannelPacketHandler(ClientPacketManager packetManager, ClientMessageManager messageManager) {
        this.packetManager = packetManager;
        this.messageManager = messageManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        packetManager.clear();
        ctx.writeAndFlush(new ClientRegisterPacketHost(packetManager.netty.getResourceBundleName()));
        log.info("new channel to " + ctx.channel().remoteAddress().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        log.finest("received " + packet.getClass().getSimpleName() + " from " + ctx.channel().remoteAddress().toString());

        if (packet instanceof ClientMessageRequestPacket) {
            messageManager.onPacketReceive((ClientMessageRequestPacket) packet, ctx);
        } else if (packet instanceof ClientMessageResponsePacket) {
            ClientMessageResponsePacket response = (ClientMessageResponsePacket) packet;

            if (packetManager.clientRequests.containsKey(response.getRequestUuid())) {
                packetManager.clientRequests.get(response.getRequestUuid()).onClientRequest(response);
            }
        } else {
            Set<PacketHandler<? extends Packet>> handlers;
            if (packetManager.handlers.containsKey(packet.getClass()) && (handlers = packetManager.handlers.get(packet.getClass())) != null) {
                for (PacketHandler handler : handlers) {
                    handler.onPacketReceive(packet, ctx);
                }
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        packetManager.channel = null;
        packetManager.netty.client.onChannelUnregistered(ctx);
        packetManager.netty.scheduleReconnect();
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

    @Override
    public Class<? extends Packet> getPacketById(int id) {
        return packetManager.getPacketById(id);
    }

    @Override
    public int getPacketId(Class<? extends Packet> packet) {
        return packetManager.getPacketId(packet);
    }

}
