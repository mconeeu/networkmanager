/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.host.network;

import group.onegaming.networkmanager.api.packet.ClientRegisterPacketHost;
import group.onegaming.networkmanager.api.packet.interfaces.PacketHandler;
import group.onegaming.networkmanager.api.packet.PacketRegisterPacketClient;
import group.onegaming.networkmanager.host.NetworkManager;
import group.onegaming.networkmanager.host.api.server.ConnectionListener;
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
