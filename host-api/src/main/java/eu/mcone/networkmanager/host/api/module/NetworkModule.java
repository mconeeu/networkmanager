/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.api.module;

import eu.mcone.networkmanager.api.messaging.request.ClientMessageRequestListener;
import eu.mcone.networkmanager.api.packet.Packet;
import eu.mcone.networkmanager.api.packet.interfaces.PacketHandler;
import eu.mcone.networkmanager.host.api.ModuleHost;
import eu.mcone.networkmanager.host.api.event.Event;
import eu.mcone.networkmanager.host.api.event.Listener;
import eu.mcone.networkmanager.host.api.server.ConnectionListener;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class NetworkModule {

    private ModuleInfo info;

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    public ModuleInfo getModuleInfo() {
        return info.clone();
    }

    public void setModuleInfo(ModuleInfo info) {
        if (this.info == null) {
            this.info = info;
        } else {
            throw new UnsupportedOperationException("ModuleInfo is already set!");
        }
    }

    protected <T extends Packet> void registerPacket(Class<T> clazz, PacketHandler<T> handler) {
        ModuleHost.getInstance().getPacketManager().registerPacket(this, clazz, handler);
    }

    protected <T extends Packet> void registerPacket(Class<T> clazz) {
        ModuleHost.getInstance().getPacketManager().registerPacket(this, clazz);
    }

    protected void registerClientMessageListener(String uri, ClientMessageRequestListener listener) {
        ModuleHost.getInstance().getClientRequestManager().registerClientMessageListener(uri, listener);
    }

    protected void registerConnectionListener(ConnectionListener listener) {
        ModuleHost.getInstance().getPacketManager().registerConnectionListener(listener);
    }

    protected void registerEventListeners(Listener<? extends Event>... listeners) {
        for (Listener<? extends Event> listener : listeners) {
            ModuleHost.getInstance().getEventManager().registerListener(listener);
        }
    }

}
