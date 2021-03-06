/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.client;

import group.onegaming.networkmanager.api.messaging.response.ClientMessageResponseListener;
import group.onegaming.networkmanager.api.packet.ClientMessageRequestPacket;
import group.onegaming.networkmanager.api.packet.ClientRegisterPacketHost;
import group.onegaming.networkmanager.api.packet.Packet;
import group.onegaming.networkmanager.api.packet.PacketRegisterPacketClient;
import group.onegaming.networkmanager.api.packet.interfaces.PacketHandler;
import group.onegaming.networkmanager.api.packet.interfaces.PacketResolver;
import group.onegaming.networkmanager.api.pipeline.FutureListeners;
import group.onegaming.networkmanager.client.api.PacketManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.*;

@Log
public class ClientPacketManager implements PacketManager, PacketResolver {

    public final ClientBootstrap netty;
    @Getter
    Channel channel;

    private final Map<Class<? extends Packet>, Integer> packetIds;
    Map<String, ClientMessageResponseListener> clientRequests;
    Map<Class<? extends Packet>, Set<PacketHandler<? extends Packet>>> handlers;

    ClientPacketManager(ClientBootstrap netty) {
        this.netty = netty;
        this.packetIds = new HashMap<>();
        this.clientRequests = new HashMap<>();
        this.handlers = new HashMap<>();

        clear();
    }

    void clear() {
        packetIds.clear();
        handlers.clear();

        packetIds.put(ClientRegisterPacketHost.class, 0);
        packetIds.put(PacketRegisterPacketClient.class, 1);

        registerPacketHandler(PacketRegisterPacketClient.class, (packet, ctx) -> {
            for (HashMap.Entry<Class<? extends Packet>, Integer> entry : packet.getRegisteredPackets().entrySet()) {
                packetIds.put(entry.getKey(), entry.getValue());
            }

            channel = ctx.channel();
            log.info("Self-registered successfully with resourceBundleName: " + netty.getResourceBundleName());
            netty.client.onChannelActive(ctx);
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

    public ChannelFuture send(Packet packet) {
        if (channel != null) {
            return channel.writeAndFlush(packet).addListener(FutureListeners.FUTURE_LISTENER);
        } else {
            throw new IllegalStateException("Could not send Client Request because Client is not connected!");
        }
    }

    public ChannelFuture sendClientRequest(ClientMessageRequestPacket packet, ClientMessageResponseListener callback) {
        clientRequests.put(packet.getRequestUuid(), callback);
        return send(packet);
    }

}
