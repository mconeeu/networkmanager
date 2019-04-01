/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class PacketRegisterPacketClient extends Packet {

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

        int amount = in.readInt();
        for (int i = 0; i < amount; i++) {
            try {
                String name = in.readUTF();
                int id = in.readInt();

                registeredPackets.put(Class.forName(name).asSubclass(Packet.class), id);
            } catch (ClassNotFoundException ignored) {}
        }
    }

}
