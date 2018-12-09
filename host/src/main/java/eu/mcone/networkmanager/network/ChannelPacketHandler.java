/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network;

import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import eu.mcone.networkmanager.api.network.packet.Packet;
import eu.mcone.networkmanager.api.network.packet.PacketResolver;
import eu.mcone.networkmanager.api.server.ConnectionListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@Log
public class ChannelPacketHandler extends SimpleChannelInboundHandler<Packet> implements PacketResolver {

    private PacketManager manager;

    public ChannelPacketHandler(PacketManager manager) {
        this.manager = manager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("new channel from " + ctx.channel().remoteAddress().toString());

        for (ConnectionListener listener : manager.listeners) {
            listener.onChannelActive(ctx);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chc, Packet packet) {
        log.info("received " + packet.getClass().getSimpleName() + " from " + chc.channel().remoteAddress().toString());

        if (manager.handlers.containsKey(packet.getClass())) {
            Set<PacketHandler<? extends Packet>> handlers;

            if ((handlers = manager.handlers.get(packet.getClass())) != null) {
                for (PacketHandler handler : handlers) {
                    handler.onPacketReceive(packet, chc);
                }
            }
        } else {
            try {
                throw new IllegalStateException("Packet " + packet.getClass().getSimpleName() + " is not registered!");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("unregister channel to " + ctx.channel().remoteAddress().toString());

        for (HashMap.Entry<UUID, Channel> entry : manager.channels.entrySet()) {
            if (entry.getValue().equals(ctx.channel())) {
                manager.channels.remove(entry.getKey());
                break;
            }
        }

        System.out.println("calling listeners");
        for (ConnectionListener listener : manager.listeners) {
            listener.onChannelUnregistered(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.severe("Netty Exception: " + cause.getMessage());
        cause.printStackTrace();

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
