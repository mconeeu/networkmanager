/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.client;

import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import eu.mcone.networkmanager.api.network.packet.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.*;

@Log
public class ClientChannelPacketHandler extends SimpleChannelInboundHandler<Packet> implements PacketResolver {

    private final ClientPacketManager manager;

    public ClientChannelPacketHandler(ClientPacketManager manager) {
        this.manager = manager;
    }

    @Override
    public void channelActive(ChannelHandlerContext chc) {
        manager.clear();
        chc.writeAndFlush(new ClientRegisterPacketHost(manager.netty.getResourceBundleName()));
        log.info("new channel to " + chc.channel().remoteAddress().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chc, Packet packet) {
        log.finest("received " + packet.getClass().getSimpleName() + " from " + chc.channel().remoteAddress().toString());

        Set<PacketHandler<? extends Packet>> handlers;
        if (manager.handlers.containsKey(packet.getClass()) && (handlers = manager.handlers.get(packet.getClass())) != null) {
            for (PacketHandler handler : handlers) {
                handler.onPacketReceive(packet, chc);
            }
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext chc) {
        manager.netty.client.onChannelUnregistered(chc);
        manager.netty.scheduleReconnect();
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
        return manager.getPacketById(id);
    }

    @Override
    public int getPacketId(Class<? extends Packet> packet) {
        return manager.getPacketId(packet);
    }

}
