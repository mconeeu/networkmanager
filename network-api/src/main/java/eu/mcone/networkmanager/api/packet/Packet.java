/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.packet;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;

import java.io.*;

@NoArgsConstructor
public abstract class Packet {

    public void write(ByteBuf byteBuf) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            onWrite(out);

            byte[] result = stream.toByteArray();
            byteBuf.writeInt(result.length);
            byteBuf.writeBytes(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read(ByteBuf byteBuf) {
        byte[] msg = new byte[byteBuf.readInt()];
        byteBuf.readBytes(msg);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(msg));
        try {
            onRead(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void onWrite(DataOutputStream out) throws IOException;

    public abstract void onRead(DataInputStream in) throws IOException;

}
