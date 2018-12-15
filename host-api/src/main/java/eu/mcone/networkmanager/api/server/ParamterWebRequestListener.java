/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Log
public abstract class ParamterWebRequestListener implements WebRequestListener {

    private static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public FullHttpResponse onRequest(FullHttpRequest request, LinkedList<String> uri) {
        return onParameterRequest(request, uri, getRequestAsJsonString(request));
    }

    protected abstract FullHttpResponse onParameterRequest(FullHttpRequest request, LinkedList<String> uri, JsonElement parameters);

    static JsonElement getRequestAsJsonString(FullHttpRequest req) {
        List<InterfaceHttpData> postParams = new HttpPostRequestDecoder(req).getBodyHttpDatas();
        if (postParams.size() > 0) {
            JsonObject result = new JsonObject();

            for (InterfaceHttpData param : postParams) {
                if (param.getHttpDataType().equals(InterfaceHttpData.HttpDataType.Attribute)) {
                    try {
                        result.addProperty(param.getName(), ((Attribute) param).getValue());
                    } catch (IOException e) {
                        log.warning("Could not read PostRequestAttribute for name "+param.getName());
                    }
                }
            }

            return result;
        } else {
            return JSON_PARSER.parse(req.content().toString(CharsetUtil.UTF_8));
        }
    }

}
