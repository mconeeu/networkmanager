/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network;

import com.google.gson.JsonObject;
import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.api.module.NetworkModule;
import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import eu.mcone.networkmanager.api.network.client.handler.WebRequestGetHandler;
import eu.mcone.networkmanager.api.network.client.handler.WebRequestHandler;
import eu.mcone.networkmanager.api.network.client.handler.WebRequestSetHandler;
import eu.mcone.networkmanager.api.network.packet.ClientRegisterPacketHost;
import eu.mcone.networkmanager.api.network.packet.Packet;
import eu.mcone.networkmanager.api.network.packet.PacketRegisterPacketClient;
import eu.mcone.networkmanager.api.network.packet.PacketResolver;
import eu.mcone.networkmanager.api.server.ServerChannelPacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;

import java.util.*;

@Log
public class ChannelPacketHandler extends SimpleChannelInboundHandler<Packet> implements ServerChannelPacketHandler, PacketResolver {

    private Map<UUID, Channel> channels;
    private Map<NetworkModule, Set<Class<? extends Packet>>> modules;
    private Map<Class<? extends Packet>, Integer> packetIds;
    private Map<Class<? extends Packet>, Set<PacketHandler<? extends Packet>>> handlers;
    private Map<String, WebRequestHandler> webHandler;

    public ChannelPacketHandler() {
        channels = new HashMap<>();
        modules = new HashMap<>();
        packetIds = new HashMap<>();
        handlers = new HashMap<>();
        webHandler = new HashMap<>();

        registerPacket(null, ClientRegisterPacketHost.class, (packet, chc) -> {
            UUID uuid = UUID.randomUUID();
            channels.put(uuid, chc.channel());

            if (!packet.getResourceBundleName().equals("eu.mcone.webapi")) {
                chc.writeAndFlush(new PacketRegisterPacketClient(packetIds));
            }
        });
        registerPacket(null, PacketRegisterPacketClient.class);
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
    public <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz, PacketHandler<T> handler) {
        if (module != null) {
            boolean contains;
            if (!(contains = modules.containsKey(module)) || !modules.get(module).contains(clazz)) {
                if (contains) {
                    modules.get(module).add(clazz);
                } else {
                    modules.put(module, new HashSet<>(Collections.singletonList(clazz)));
                }
            } else {
                log.severe("The packet " + clazz.getSimpleName() + " was already registered");
                return;
            }
        }

        int last = (packetIds.size() > 0) ? Collections.max(packetIds.entrySet(), HashMap.Entry.comparingByValue()).getValue() : -1;
        log.fine("Registering packet " + clazz.getSimpleName() + " with packet id " + (++last));
        packetIds.put(clazz, last);
        handlers.put(clazz, new HashSet<>(Collections.singletonList(handler)));
    }

    @Override
    public <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz) {
        boolean contains;
        if (!(contains = modules.containsKey(module)) || !modules.get(module).contains(clazz)) {
            if (contains) {
                modules.get(module).add(clazz);
            } else {
                modules.put(module, new HashSet<>(Collections.singletonList(clazz)));
            }

            int last = (packetIds.size() > 0) ? Collections.max(packetIds.entrySet(), HashMap.Entry.comparingByValue()).getValue() : -1;
            log.fine("Registering packet " + clazz.getSimpleName() + " with packet id " + (++last));
            packetIds.put(clazz, last);
            handlers.put(clazz, new HashSet<>());
        } else {
            log.severe("The packet " + clazz.getSimpleName() + " was already registered");
        }
    }

    public void unregisterPackets(NetworkModule module) {
        if (modules.containsKey(module)) {
            for (Class<? extends Packet> packet : modules.get(module)) {
                packetIds.remove(packet);
                handlers.remove(packet);
            }
            modules.remove(module);
        }
    }

    @Override
    public <T extends Packet> void registerAdditionalPacketHandler(Class<T> clazz, PacketHandler<T> handler) throws IllegalStateException {
        if (handlers.containsKey(clazz)) {
            handlers.get(clazz).add(handler);
        } else {
            throw new IllegalStateException("The packet " + clazz.getSimpleName() + " is not yet registered!");
        }
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

        if (this.handlers.containsKey(packet.getClass())) {
            Set<PacketHandler<? extends Packet>> handlers;

            if ((handlers = this.handlers.get(packet.getClass())) != null) {
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
        cause.printStackTrace();
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
