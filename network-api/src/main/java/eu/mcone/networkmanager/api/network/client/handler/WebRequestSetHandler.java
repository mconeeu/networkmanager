/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.network.client.handler;

import com.google.gson.JsonElement;

public interface WebRequestSetHandler extends WebRequestHandler {

    void onRequest(JsonElement jsonElement);

}
