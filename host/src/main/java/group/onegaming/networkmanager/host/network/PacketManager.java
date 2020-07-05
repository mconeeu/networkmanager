/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.host.network;

import group.onegaming.networkmanager.api.messaging.response.ClientMessageResponseListener;
import group.onegaming.networkmanager.api.packet.ClientMessageRequestPacket;
import group.onegaming.networkmanager.api.packet.ClientRegisterPacketHost;
import group.onegaming.networkmanager.api.packet.Packet;
import group.onegaming.networkmanager.api.packet.PacketRegisterPacketClient;
import group.onegaming.networkmanager.api.packet.interfaces.PacketHandler;
import group.onegaming.networkmanager.api.packet.interfaces.PacketResolver;
import group.onegaming.networkmanager.api.pipeline.FutureListeners;
import group.onegaming.networkmanager.host.api.module.NetworkModule;
import group.onegaming.networkmanager.host.api.server.ConnectionListener;
import group.onegaming.networkmanager.host.api.server.ServerPacketManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.java.Log;

import java.util.*;

@Log
public class PacketManager implements ServerPacketManager, PacketResolver {

    private Map<NetworkModule, Set<Class<? extends Packet>>> modules;
    Map<Class<? extends Packet>, Integer> packetIds;

    Map<Channel, String> clients;
    Map<String, ClientMessageResponseListener> clientRequests;
    Map<Class<? extends Packet>, Set<PacketHandler<? extends Packet>>> handlers;
    List<ConnectionListener> listeners;

    public PacketManager() {
        clients = new HashMap<>();
        clientRequests = new HashMap<>();
        modules = new HashMap<>();
        packetIds = new HashMap<>();
        handlers = new HashMap<>();
        listeners = new ArrayList<>();

        registerPacket(null, 0, ClientRegisterPacketHost.class, new ClientRegisterHandler());
        registerPacket(null, 1, PacketRegisterPacketClient.class);
        registerPacket(null, 2, ClientMessageRequestPacket.class);
    }

    @Override
    public void registerConnectionListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz, PacketHandler<T> handler) {
        registerPacket(module, clazz);
        handlers.put(clazz, new HashSet<>(Collections.singletonList(handler)));
    }

    @Override
    public <T extends Packet> void registerPacket(NetworkModule module, int id, Class<T> clazz, PacketHandler<T> handler) {
        registerPacket(module, id, clazz);
        handlers.put(clazz, new HashSet<>(Collections.singletonList(handler)));
    }

    @Override
    public <T extends Packet> boolean registerPacket(NetworkModule module, int id, Class<T> clazz) {
        if (!packetIds.containsValue(id)) {
            boolean contains;
            if (!(contains = modules.containsKey(module)) || !modules.get(module).contains(clazz)) {
                if (contains) {
                    modules.get(module).add(clazz);
                } else {
                    modules.put(module, new HashSet<>(Collections.singletonList(clazz)));
                }

                log.fine("Registering packet " + clazz.getSimpleName() + " with packet id " + id);
                packetIds.put(clazz, id);
                handlers.put(clazz, new HashSet<>());
                return true;
            } else {
                log.severe("The packet " + clazz.getSimpleName() + " was already registered");
            }
        }

        return false;
    }

    @Override
    public <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz) {
        boolean done = false;
        Random random = new Random();
        while (!done) {
            done = registerPacket(module, random.nextInt(899) + 100, clazz);
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
    public <T extends Packet> void registerAdditionalPacketHandler(Class<T> clazz, PacketHandler<T> handler) throws
            IllegalStateException {
        if (handlers.containsKey(clazz)) {
            handlers.get(clazz).add(handler);
        } else {
            throw new IllegalStateException("The packet " + clazz.getSimpleName() + " is not yet registered!");
        }
    }

    @Override
    public Collection<Channel> getClients() {
        return clients.keySet();
    }

    @Override
    public Channel getClient(String resourceBundleName) {
        for (Map.Entry<Channel, String> e : clients.entrySet()) {
            if (e.getValue().equalsIgnoreCase(resourceBundleName)) {
                return e.getKey();
            }
        }
        return null;
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
    public ChannelFuture send(Channel channel, Packet packet) {
        if (channel != null) {
            return channel.writeAndFlush(packet).addListener(FutureListeners.FUTURE_LISTENER);
        } else {
            throw new IllegalStateException("Could not send Client Request because Client is not connected!");
        }
    }

    @Override
    public ChannelFuture sendClientRequest(Channel channel, ClientMessageRequestPacket
            packet, ClientMessageResponseListener callback) {
        clientRequests.put(packet.getRequestUuid(), callback);
        return send(channel, packet);
    }

}
