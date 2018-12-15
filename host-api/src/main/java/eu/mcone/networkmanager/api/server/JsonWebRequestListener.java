/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.server;

import com.google.gson.JsonElement;
import eu.mcone.networkmanager.api.ModuleHost;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.LinkedList;

public abstract class JsonWebRequestListener implements WebRequestListener {

    @Override
    public FullHttpResponse onRequest(FullHttpRequest request, LinkedList<String> uri) {
        FullHttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(
                        ModuleHost.getInstance().getGson().toJson(onJsonRequest(request, uri)),
                        CharsetUtil.UTF_8
                )
        );
        res.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/json");
        return res;
    }

    protected abstract JsonElement onJsonRequest(FullHttpRequest request, LinkedList<String> uri);

}
