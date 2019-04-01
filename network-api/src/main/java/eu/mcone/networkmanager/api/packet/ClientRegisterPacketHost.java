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

@AllArgsConstructor
@NoArgsConstructor
public class ClientRegisterPacketHost extends Packet {

    @Getter
    private String resourceBundleName;

    @Override
    public void onWrite(DataOutputStream out) throws IOException {
        out.writeUTF(resourceBundleName);
    }

    @Override
    public void onRead(DataInputStream in) throws IOException {
        resourceBundleName = in.readUTF();
    }

}
