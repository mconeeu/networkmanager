/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.server;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.HttpMethod;

import java.util.LinkedList;

public interface ClientRequestListener {

    HostResponse onClientRequest(HttpMethod method, LinkedList<String> uri, JsonElement data);

}
