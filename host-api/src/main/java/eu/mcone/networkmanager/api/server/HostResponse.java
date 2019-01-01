/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class HostResponse {

    public HostResponse(HttpResponseStatus code, String error) {
        this(code, new JsonObject(), error);
    }

    public HostResponse(HttpResponseStatus code, JsonElement data) {
        this(code, data, null);
    }

    public HostResponse(HttpResponseStatus code, JsonElement data, String error) {
        this.code = code;
        this.data = data;
        this.error = error;
    }

    private HttpResponseStatus code;
    private JsonElement data;
    private String error;

}
