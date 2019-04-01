/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.packet;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@NoArgsConstructor
@Getter
public class ClientMessageResponsePacket extends Packet {

    private String requestUuid;
    private HttpResponseStatus status;

    public ClientMessageResponsePacket(String requestUuid, HttpResponseStatus status) {
        this.requestUuid = requestUuid;
        this.status = status;
    }

    @Override
    public void onWrite(DataOutputStream out) throws IOException {
        out.writeUTF(requestUuid);
        out.writeShort((short) status.code());
    }

    @Override
    public void onRead(DataInputStream in) throws IOException {
        requestUuid = in.readUTF();
        status = HttpResponseStatus.valueOf(in.readShort());
    }

    public static ClientMessageResponsePacket success(String requestUuid) {
        return new ClientMessageResponsePacket(requestUuid, HttpResponseStatus.OK);
    }

    public static ClientMessageResponsePacket badRequest(String requestUuid) {
        return new ClientMessageResponsePacket(requestUuid, HttpResponseStatus.BAD_REQUEST);
    }

    public static ClientMessageResponsePacket serverError(String requestUuid) {
        return new ClientMessageResponsePacket(requestUuid, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    public static ClientMessageResponsePacket noPermission(String requestUuid) {
        return new ClientMessageResponsePacket(requestUuid, HttpResponseStatus.UNAUTHORIZED);
    }

}
