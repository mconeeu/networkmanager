/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.packet;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class ClientMessageRequestPacket extends Packet {

    private String requestUuid;
    private String uri;
    private HttpMethod method;

    public ClientMessageRequestPacket(String uri, HttpMethod method) {
        this.requestUuid = UUID.randomUUID().toString();
        this.uri = uri;
        this.method = method;
    }

    @Override
    public void onWrite(DataOutputStream out) throws IOException {
        out.writeUTF(requestUuid);
        out.writeUTF(uri);
        out.writeUTF(method.name());
    }

    @Override
    public void onRead(DataInputStream in) throws IOException {
        requestUuid = in.readUTF();
        uri = in.readUTF();
        method = HttpMethod.valueOf(in.readUTF());
    }

}
