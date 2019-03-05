/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.network;

import eu.mcone.networkmanager.api.packet.ClientRegisterPacketHost;
import eu.mcone.networkmanager.api.packet.interfaces.PacketHandler;
import eu.mcone.networkmanager.api.packet.PacketRegisterPacketClient;
import eu.mcone.networkmanager.host.NetworkManager;
import eu.mcone.networkmanager.host.api.server.ConnectionListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.java.Log;

@Log
public class ClientRegisterHandler implements PacketHandler<ClientRegisterPacketHost> {

    @Override
    public void onPacketReceive(ClientRegisterPacketHost packet, ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new PacketRegisterPacketClient(NetworkManager.getManager().getPacketManager().packetIds));
        NetworkManager.getManager().getPacketManager().clients.put(ctx.channel(), packet.getResourceBundleName());

        log.info("Client registered successfully with resourceBundleName: "+packet.getResourceBundleName());
        for (ConnectionListener listener : NetworkManager.getManager().getPacketManager().listeners) {
            listener.onChannelActive(packet.getResourceBundleName(), ctx);
        }
    }

}
