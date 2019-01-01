/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.packet;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class ClientRequestPacketHost extends Packet {

    private UUID requestUuid;
    private String[] uri;
    private HttpMethod method;
    private String data;

    public ClientRequestPacketHost(String[] uri, HttpMethod method, JsonElement element) {
        this.requestUuid = UUID.randomUUID();
        this.uri = uri;
        this.method = method;
        this.data = element.toString();
    }

    @Override
    public void onWrite(DataOutputStream out) throws IOException {
        out.writeUTF(requestUuid.toString());

        out.writeShort(uri.length);
        for (String part : uri) {
            out.writeUTF(part);
        }

        out.writeUTF(method.toString());
        out.writeUTF(data);
    }

    @Override
    public void onRead(DataInputStream in) throws IOException {
        requestUuid = UUID.fromString(in.readUTF());

        short length = in.readShort();
        uri = new String[length];
        for (int i = 0; i < length; i++) {
            uri[i] = in.readUTF();
        }

        method = HttpMethod.valueOf(in.readUTF());
        data = in.readUTF();
    }

}
