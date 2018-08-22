/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.packet;

import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.*;
import java.util.List;

public abstract class Packet {

    private static Packet INSTANCE;

    protected Packet() {
        INSTANCE = this;
    }

    public static void addHandler(PacketHandler handler) {
        INSTANCE.getHandlerList().add(handler);
    }

    public static void removeHandler(PacketHandler handler) {
        INSTANCE.getHandlerList().remove(handler);
    }

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

    public abstract List<PacketHandler> getHandlerList();

    public abstract void onWrite(DataOutputStream out) throws IOException;

    public abstract void onRead(DataInputStream out) throws IOException;

}
