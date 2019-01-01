/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.packet;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class HostResponsePacketClient extends Packet {

    private UUID requestUuid;
    private HttpResponseStatus status;
    private String data;
    private String error;

    public HostResponsePacketClient(UUID requestUuid, HttpResponseStatus status, JsonElement date) {
        this(requestUuid, status, date, "");
    }

    public HostResponsePacketClient(UUID requestUuid, HttpResponseStatus status, JsonElement date, String error) {
        this.requestUuid = requestUuid;
        this.status = status;
        this.data = date.toString();
        this.error = error != null ? error : "";
    }

    @Override
    public void onWrite(DataOutputStream out) throws IOException {
        out.writeUTF(requestUuid.toString());
        out.writeShort((short) status.code());
        out.writeUTF(data);
        out.writeUTF(error);
    }

    @Override
    public void onRead(DataInputStream in) throws IOException {
        requestUuid = UUID.fromString(in.readUTF());
        status = HttpResponseStatus.valueOf(in.readShort());
        data = in.readUTF();
        error = in.readUTF();
    }

}
