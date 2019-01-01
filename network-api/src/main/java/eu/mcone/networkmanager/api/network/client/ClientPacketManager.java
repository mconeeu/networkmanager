/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.client;

import eu.mcone.networkmanager.api.network.packet.PacketHandler;
import eu.mcone.networkmanager.api.network.packet.ClientRegisterPacketHost;
import eu.mcone.networkmanager.api.network.packet.Packet;
import eu.mcone.networkmanager.api.network.packet.PacketRegisterPacketClient;
import eu.mcone.networkmanager.api.network.packet.PacketResolver;
import lombok.extern.java.Log;

import java.util.*;

@Log
public class ClientPacketManager implements PacketResolver {

    public final ClientBootstrap netty;

    private Map<Class<? extends Packet>, Integer> packetIds;
    Map<Class<? extends Packet>, Set<PacketHandler<? extends Packet>>> handlers;

    ClientPacketManager(ClientBootstrap netty) {
        this.netty = netty;
        this.packetIds = new HashMap<>();
        this.handlers = new HashMap<>();

        clear();
    }

    void clear() {
        packetIds.clear();
        handlers.clear();

        packetIds.put(ClientRegisterPacketHost.class, 0);
        packetIds.put(PacketRegisterPacketClient.class, 1);

        registerPacketHandler(PacketRegisterPacketClient.class, (packet, chc) -> {
            for (HashMap.Entry<Class<? extends Packet>, Integer> entry : packet.getRegisteredPackets().entrySet()) {
                packetIds.put(entry.getKey(), entry.getValue());
            }

            netty.client.onChannelActive(chc);
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

    public <T extends Packet> void registerPacketHandler(Class<T> clazz, PacketHandler<T> handler) {
        if (handlers.containsKey(clazz)) {
            handlers.get(clazz).add(handler);
        } else {
            handlers.put(clazz, new HashSet<>(Collections.singletonList(handler)));
        }
    }

}
