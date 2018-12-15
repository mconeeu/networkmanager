/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.api.module.NetworkModule;
import eu.mcone.networkmanager.api.server.JsonWebRequestListener;
import eu.mcone.networkmanager.api.server.ServerWebRequestManager;
import eu.mcone.networkmanager.api.server.WebRequestListener;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class WebRequestManager implements ServerWebRequestManager {

    @Getter
    private final Map<String[], WebRequestListener> listeners;

    public WebRequestManager() {
        this.listeners = new HashMap<>();
        registerWebRequestListener(new String[]{"status"}, new JsonWebRequestListener() {
            @Override
            protected JsonElement onJsonRequest(FullHttpRequest request, LinkedList<String> uri) {
                JsonObject result = new JsonObject();
                result.addProperty("host", true);

                JsonObject modules = new JsonObject();
                for (NetworkModule module : NetworkManager.getManager().getModuleManager().getLoadedModules()) {
                    modules.addProperty(module.getModuleInfo().getName(), true);
                }

                result.add("modules", modules);
                return result;
            }
        });
    }

    public void registerWebRequestListener(String[] uri, WebRequestListener listener) throws IllegalStateException {
        for (String[] s : listeners.keySet()) {
            if (Arrays.equals(s, uri)) {
                throw new IllegalStateException("For the uri "+Arrays.asList(uri)+" is already one listener registered!");
            }
        }

        listeners.put(uri, listener);
    }

}
