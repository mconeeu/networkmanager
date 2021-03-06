/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.host.module;

import group.onegaming.networkmanager.host.api.ModuleHost;
import group.onegaming.networkmanager.host.api.event.Event;
import group.onegaming.networkmanager.host.api.event.Listener;
import group.onegaming.networkmanager.host.api.module.NetworkModule;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventManager implements group.onegaming.networkmanager.host.api.manager.EventManager {

    private final Map<Class<? extends Event>, Set<Listener<? extends Event>>> listeners;

    public EventManager() {
        this.listeners = new HashMap<>();
    }

    public void registerListener(Listener<? extends Event> listener) {
        try {
            Method m = listener.getClass().getMethod("on", Event.class);
            Set<Listener<? extends Event>> methodSet = listeners.getOrDefault(m.getParameterTypes()[0], new HashSet<>());
            methodSet.add(listener);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public <E extends Event> void callEvent(NetworkModule module, E event) {
        event.setModule(module);

        ModuleHost.getInstance().getThreadPool().execute(() -> {
            if (listeners.containsKey(event.getClass())) {
                for (Listener listener : listeners.get(event.getClass())) {
                    listener.on(event);
                }
            }
        });
    }

}
