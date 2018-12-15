/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.server;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.LinkedList;

public abstract class ParameterJsonWebRequestListener extends JsonWebRequestListener {

    @Override
    protected JsonElement onJsonRequest(FullHttpRequest request, LinkedList<String> uri) {
        return onParameterJsonRequest(request, uri, ParamterWebRequestListener.getRequestAsJsonString(request));
    }

    protected abstract JsonElement onParameterJsonRequest(FullHttpRequest request, LinkedList<String> uri, JsonElement parameters);

}
