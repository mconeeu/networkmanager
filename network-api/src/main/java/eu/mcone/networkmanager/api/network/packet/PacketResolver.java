/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.packet;

public interface PacketResolver {

    Class<? extends Packet> getPacketById(int id);

    int getPacketId(Class<? extends Packet> packet);

}
