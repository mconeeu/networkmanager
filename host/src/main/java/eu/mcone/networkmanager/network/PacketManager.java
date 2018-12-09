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
import eu.mcone.networkmanager.api.server.ConnectionListener;
import eu.mcone.networkmanager.api.server.ServerPacketManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.java.Log;

import java.util.*;

@Log
public class PacketManager implements ServerPacketManager, PacketResolver {

    private Map<NetworkModule, Set<Class<? extends Packet>>> modules;
    private Map<Class<? extends Packet>, Integer> packetIds;
    private Map<String, WebRequestHandler> webHandler;

    Map<UUID, Channel> channels;
    Map<Class<? extends Packet>, Set<PacketHandler<? extends Packet>>> handlers;
    List<ConnectionListener> listeners;

    public PacketManager() {
        modules = new HashMap<>();
        packetIds = new HashMap<>();
        webHandler = new HashMap<>();
        channels = new HashMap<>();
        handlers = new HashMap<>();
        listeners = new ArrayList<>();

        registerPacket(null, ClientRegisterPacketHost.class, (packet, chc) -> {
            UUID uuid = UUID.randomUUID();
            channels.put(uuid, chc.channel());

            if (!packet.getResourceBundleName().equals("eu.mcone.webapi")) {
                chc.writeAndFlush(new PacketRegisterPacketClient(packetIds));
            }
        });
        registerPacket(null, PacketRegisterPacketClient.class);
    }

    @Override
    public void registerConnectionListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void writeAndFlush(UUID channelUuid, Object object) {
        if (channels.containsKey(channelUuid)) {
            channels.get(channelUuid).writeAndFlush(object);
        }
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
    public void registerWebRequestHandler(String name, WebRequestHandler handler) {
        webHandler.put(name, handler);
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
    public Channel getChannel(UUID uuid) {
        return channels.getOrDefault(uuid, null);
    }

    @Override
    public Collection<Channel> getChannels() {
        return channels.values();
    }

}
