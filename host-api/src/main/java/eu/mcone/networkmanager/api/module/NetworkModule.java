/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.module;

import eu.mcone.networkmanager.api.ModuleHost;
import eu.mcone.networkmanager.api.network.client.handler.PacketHandler;
import eu.mcone.networkmanager.api.network.packet.Packet;
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

}
