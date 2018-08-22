/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.packet;

import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ClientRegisterPacketHost extends Packet {

    private static List<PacketHandler> handlerList = new ArrayList<>();
    @Override
    public List<PacketHandler> getHandlerList() {
        return handlerList;
    }

    @Getter
    private String resourceBundleName;

    @Override
    public void onWrite(DataOutputStream out) throws IOException {
        out.writeUTF(resourceBundleName);
    }

    @Override
    public void onRead(DataInputStream out) throws IOException {
        resourceBundleName = out.readUTF();
    }
}
