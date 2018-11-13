/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@NoArgsConstructor
public class HostRequestPacketWebAPI extends Packet {

    private String json;

    @Override
    public void onWrite(DataOutputStream out) {}

    @Override
    public void onRead(DataInputStream in) throws IOException {
        json = in.readUTF();
    }

}
