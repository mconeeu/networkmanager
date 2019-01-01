/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network;

import com.google.gson.JsonObject;
import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.api.module.NetworkModule;
import eu.mcone.networkmanager.api.server.ClientRequestListener;
import eu.mcone.networkmanager.api.server.HostResponse;
import eu.mcone.networkmanager.api.server.ServerClientRequestManager;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClientRequestManager implements ServerClientRequestManager {

    @Getter
    private final Map<String[], ClientRequestListener> listeners;

    public ClientRequestManager() {
        this.listeners = new HashMap<>();

        registerClientRequestListener(new String[]{"status"}, (method, uri, data) -> {
            JsonObject result = new JsonObject();
            result.addProperty("host", true);

            JsonObject modules = new JsonObject();
            for (NetworkModule module : NetworkManager.getManager().getModuleManager().getLoadedModules()) {
                modules.addProperty(module.getModuleInfo().getName(), true);
            }

            result.add("modules", modules);
            return new HostResponse(HttpResponseStatus.OK, result);
        });
    }

    public void registerClientRequestListener(String[] uri, ClientRequestListener listener) throws IllegalStateException {
        for (String[] s : listeners.keySet()) {
            if (Arrays.equals(s, uri)) {
                throw new IllegalStateException("For the uri "+Arrays.asList(uri)+" is already one listener registered!");
            }
        }

        listeners.put(uri, listener);
    }

}
