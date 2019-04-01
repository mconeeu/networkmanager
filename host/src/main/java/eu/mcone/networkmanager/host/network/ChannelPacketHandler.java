/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.network;

import eu.mcone.networkmanager.api.packet.ClientMessageRequestPacket;
import eu.mcone.networkmanager.api.packet.ClientMessageResponsePacket;
import eu.mcone.networkmanager.api.packet.Packet;
import eu.mcone.networkmanager.api.packet.interfaces.PacketHandler;
import eu.mcone.networkmanager.api.packet.interfaces.PacketResolver;
import eu.mcone.networkmanager.host.NetworkManager;
import eu.mcone.networkmanager.host.api.server.ConnectionListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Set;

@Log
public class ChannelPacketHandler extends SimpleChannelInboundHandler<Packet> implements PacketResolver {

    private PacketManager manager;

    ChannelPacketHandler(PacketManager manager) {
        this.manager = manager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("new channel from " + ctx.channel().remoteAddress().toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        log.info("received " + packet.getClass().getSimpleName() + " from " + ctx.channel().remoteAddress().toString());

        if (packet instanceof ClientMessageRequestPacket) {
            NetworkManager.getManager().getClientRequestManager().onPacketReceive((ClientMessageRequestPacket) packet, ctx);
        } else if (packet instanceof ClientMessageResponsePacket) {
            ClientMessageResponsePacket response = (ClientMessageResponsePacket) packet;

            if (manager.clientRequests.containsKey(response.getRequestUuid())) {
                manager.clientRequests.get(response.getRequestUuid()).onClientRequest(response);
            }
        } else {
            if (manager.handlers.containsKey(packet.getClass())) {
                Set<PacketHandler<? extends Packet>> handlers;

                if ((handlers = manager.handlers.get(packet.getClass())) != null) {
                    for (PacketHandler handler : handlers) {
                        handler.onPacketReceive(packet, ctx);
                    }
                }
            } else {
                log.severe("Packet " + packet.getClass().getSimpleName() + " is not registered!");
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("unregister channel to " + ctx.channel().remoteAddress().toString());

        manager.clients.remove(ctx.channel());
        for (ConnectionListener listener : manager.listeners) {
            listener.onChannelUnregistered(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.severe(cause.getMessage() + " ("+ctx.channel().remoteAddress()+")");
        } else {
            log.severe("Netty Exception:");
            cause.printStackTrace();
        }

        for (ConnectionListener listener : manager.listeners) {
            listener.exceptionCaught(ctx, cause);
        }
    }

    @Override
    public Class<? extends Packet> getPacketById(int id) {
        return manager.getPacketById(id);
    }

    @Override
    public int getPacketId(Class<? extends Packet> packet) {
        return manager.getPacketId(packet);
    }

}
