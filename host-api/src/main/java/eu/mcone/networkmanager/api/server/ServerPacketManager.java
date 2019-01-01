/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.server;

import eu.mcone.networkmanager.api.module.NetworkModule;
import eu.mcone.networkmanager.api.network.packet.Packet;
import eu.mcone.networkmanager.api.network.packet.PacketHandler;

public interface ServerPacketManager {

    void registerConnectionListener(ConnectionListener listener);

    <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz, PacketHandler<T> handler);

    <T extends Packet> void registerPacket(NetworkModule module, Class<T> clazz);

    <T extends Packet> void registerAdditionalPacketHandler(Class<T> clazz, PacketHandler<T> handler) throws IllegalStateException;

}
