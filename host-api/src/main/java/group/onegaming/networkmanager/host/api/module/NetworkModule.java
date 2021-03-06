/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.host.api.module;

import group.onegaming.networkmanager.api.messaging.request.ClientMessageRequestListener;
import group.onegaming.networkmanager.api.packet.Packet;
import group.onegaming.networkmanager.api.packet.interfaces.PacketHandler;
import group.onegaming.networkmanager.core.api.console.CommandExecutor;
import group.onegaming.networkmanager.host.api.ModuleHost;
import group.onegaming.networkmanager.host.api.event.Event;
import group.onegaming.networkmanager.host.api.event.Listener;
import group.onegaming.networkmanager.host.api.server.ConnectionListener;
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

    public <E extends Event> void callEvent(E event) {
        ModuleHost.getInstance().getEventManager().callEvent(this, event);
    }

    protected void registerCommand(String cmd, CommandExecutor commandExecutor) {
        ModuleHost.getInstance().getConsoleReader().registerCommand(getModuleInfo().getName(), cmd, commandExecutor);
    }
}
