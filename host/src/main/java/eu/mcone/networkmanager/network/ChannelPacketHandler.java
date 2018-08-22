/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network;

import com.google.gson.JsonObject;
import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import eu.mcone.networkmanager.api.network.client.handler.WebRequestGetHandler;
import eu.mcone.networkmanager.api.network.client.handler.WebRequestHandler;
import eu.mcone.networkmanager.api.network.client.handler.WebRequestSetHandler;
import eu.mcone.networkmanager.api.network.packet.ClientRegisterPacketHost;
import eu.mcone.networkmanager.api.network.packet.Packet;
import eu.mcone.networkmanager.api.network.packet.PacketRegisterPacketClient;
import eu.mcone.networkmanager.api.network.packet.PacketResolver;
import eu.mcone.networkmanager.api.network.server.ServerChannelPacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;

import java.util.*;

@Log
public class ChannelPacketHandler extends SimpleChannelInboundHandler<Packet> implements ServerChannelPacketHandler, PacketResolver {

    private Map<UUID, Channel> channels;
    private Map<Class<? extends Packet>, Integer> packetIds;
    private Map<String, WebRequestHandler> webHandler;

    public ChannelPacketHandler() {
        channels = new HashMap<>();
        packetIds = new HashMap<>();
        webHandler = new HashMap<>();

        packetIds.put(ClientRegisterPacketHost.class, 0);
        packetIds.put(PacketRegisterPacketClient.class, 1);

        ClientRegisterPacketHost.addHandler((PacketHandler<ClientRegisterPacketHost>) (packet, chc) -> {
            UUID uuid = UUID.randomUUID();
            channels.put(uuid, chc.channel());

            if (!packet.getResourceBundleName().equals("eu.mcone.webapi")) {
                chc.writeAndFlush(new PacketRegisterPacketClient(packetIds));
            }
        });
    }

    public void onWebRequest(String json, ChannelHandlerContext chc) {
        JsonObject object = NetworkManager.getManager().getJsonParser().parse(json).getAsJsonObject();

        if (!object.has("method") || !object.has("handler") || !object.has("request")) {
            chc.writeAndFlush("{\"msg\":\"error\", \"result\":\"request does not include required fields method, handler & request!\"}");
            return;
        }

        switch (object.get("method").getAsString()) {
            case "GET": {
                WebRequestHandler handler = webHandler.getOrDefault(object.get("handler").getAsString(), null);

                if (handler != null) {
                    try {
                        chc.writeAndFlush("{\"msg\":\"success\", \"result\":" + ((WebRequestGetHandler) handler).onRequest(object.get("request")) + "}");
                    } catch (ClassCastException e) {
                        chc.writeAndFlush("{\"msg\":\"error\", \"result\":\"handler is not a GET handler!\"}");
                    }
                }
                break;
            }
            case "SET": {
                WebRequestHandler handler = webHandler.getOrDefault(object.get("handler").getAsString(), null);

                if (handler != null) {
                    try {
                        ((WebRequestSetHandler) handler).onRequest(object.get("request"));
                    } catch (ClassCastException e) {
                        chc.writeAndFlush("{\"msg\":\"error\", \"result\":\"handler is not a SET handler!\"}");
                    }
                }
                break;
            }
        }
    }

    @Override
    public void writeAndFlush(UUID channelUuid, Object object) {
        if (channels.containsKey(channelUuid)) {
            channels.get(channelUuid).writeAndFlush(object);
        }
    }

    @Override
    public void registerWebRequestHandler(String name, WebRequestHandler handler) {
        webHandler.put(name, handler);
    }

    @Override
    public void registerPacket(Class<? extends Packet> clazz) {
        int last = (packetIds.size() > 0) ? Collections.max(packetIds.entrySet(), HashMap.Entry.comparingByValue()).getValue() : -1;
        packetIds.put(clazz, ++last);
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
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("new channel from " + ctx.channel().remoteAddress().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chc, Packet packet) {
        log.info("received " + packet.getClass().getSimpleName() + " from " + chc.channel().remoteAddress().toString());

        for (PacketHandler handler : packet.getHandlerList()) {
            handler.onPacketReceive(packet, chc);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("unregister channel to " + ctx.channel().remoteAddress().toString());

        for (HashMap.Entry<UUID, Channel> entry : channels.entrySet()) {
            if (entry.getValue().equals(ctx.channel())) {
                channels.remove(entry.getKey());
                return;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.severe("Netty Exception: " + cause.getMessage());
    }

    @Override
    public Channel getChannel(UUID uuid) {
        return channels.getOrDefault(uuid, null);
    }

    @Override
    public Collection<Channel> getChannels() {
        return channels.values();
    }

}
