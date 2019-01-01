/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network;

import eu.mcone.networkmanager.api.module.NetworkModule;
import eu.mcone.networkmanager.api.network.packet.*;
import eu.mcone.networkmanager.api.server.ConnectionListener;
import eu.mcone.networkmanager.api.server.ServerPacketManager;
import lombok.extern.java.Log;

import java.util.*;

@Log
public class PacketManager implements ServerPacketManager, PacketResolver {

    private Map<NetworkModule, Set<Class<? extends Packet>>> modules;
    private Map<Class<? extends Packet>, Integer> packetIds;

    Map<Class<? extends Packet>, Set<PacketHandler<? extends Packet>>> handlers;
    List<ConnectionListener> listeners;

    public PacketManager(ClientRequestManager manager) {
        modules = new HashMap<>();
        packetIds = new HashMap<>();
        handlers = new HashMap<>();
        listeners = new ArrayList<>();

        registerPacket(null, ClientRegisterPacketHost.class, (packet, chc) -> chc.writeAndFlush(new PacketRegisterPacketClient(packetIds)));
        registerPacket(null, PacketRegisterPacketClient.class);
        registerPacket(null, ClientRequestPacketHost.class, new ClientRequestHandler(manager));
        registerPacket(null, HostResponsePacketClient.class);
    }

    @Override
    public void registerConnectionListener(ConnectionListener listener) {
        listeners.add(listener);
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

}
