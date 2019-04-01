/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.packet.interfaces;

import eu.mcone.networkmanager.api.packet.Packet;

public interface PacketResolver {

    Class<? extends Packet> getPacketById(int id);

    int getPacketId(Class<? extends Packet> packet);

}
