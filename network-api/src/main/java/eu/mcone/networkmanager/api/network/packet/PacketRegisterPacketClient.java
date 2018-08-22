/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.packet;

import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class PacketRegisterPacketClient extends Packet {

    private static List<PacketHandler> handlerList = new ArrayList<>();
    @Override
    public List<PacketHandler> getHandlerList() {
        return handlerList;
    }

    @Getter
    private Map<Class<? extends Packet>, Integer> registeredPackets;

    @Override
    public void onWrite(DataOutputStream out) throws IOException {
        out.writeInt(registeredPackets.size());

        for (HashMap.Entry<Class<? extends Packet>, Integer> entry : registeredPackets.entrySet()) {
            out.writeUTF(entry.getKey().getName());
            out.writeInt(entry.getValue());
        }
    }

    @Override
    public void onRead(DataInputStream in) throws IOException {
        registeredPackets = new HashMap<>();

        for (int i = 0; i < in.readInt(); i++) {
            try {
                registeredPackets.put(Class.forName(in.readUTF()).asSubclass(Packet.class), in.readInt());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
