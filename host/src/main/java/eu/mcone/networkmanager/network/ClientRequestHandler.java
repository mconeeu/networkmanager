/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network;

import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.api.network.packet.ClientRequestPacketHost;
import eu.mcone.networkmanager.api.network.packet.HostResponsePacketClient;
import eu.mcone.networkmanager.api.network.packet.PacketHandler;
import eu.mcone.networkmanager.api.server.ClientRequestListener;
import eu.mcone.networkmanager.api.server.HostResponse;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class ClientRequestHandler implements PacketHandler<ClientRequestPacketHost> {

    private final ClientRequestManager manager;

    ClientRequestHandler(ClientRequestManager manager) {
        this.manager = manager;
    }

    @Override
    public void onPacketReceive(ClientRequestPacketHost packet, ChannelHandlerContext chc) {
        LinkedList<String> uri = new LinkedList<>(Arrays.asList(packet.getUri()));

        ClientRequestListener listener = null;
        listenerLoop:
        for (Map.Entry<String[], ClientRequestListener> e : manager.getListeners().entrySet()) {
            if (e.getKey().length == uri.size()) {
                for (int i = 0; i < uri.size(); i++) {
                    if (i <= (e.getKey().length - 1)) {
                        if (!e.getKey()[i].startsWith(":") && !e.getKey()[i].equalsIgnoreCase(uri.get(i))) {
                            continue listenerLoop;
                        }
                    } else {
                        continue listenerLoop;
                    }
                }
            } else {
                continue;
            }

            listener = e.getValue();
            break;
        }

        if (listener != null) {
            HostResponse res = listener.onClientRequest(packet.getMethod(), uri, NetworkManager.getManager().getJsonParser().parse(packet.getData()));

            if (res != null) {
                chc.writeAndFlush(
                        new HostResponsePacketClient(packet.getRequestUuid(), res.getCode(), res.getData(), res.getError())
                ).addListener(future -> {
                    if (!future.isSuccess() || future.isCancelled()) {
                        System.err.println("Netty Flush Operation failed:" +
                                "\nisDone ? " + future.isDone() + ", " +
                                "\nisSuccess ? " + future.isSuccess() + ", " +
                                "\ncause : " + future.cause() + ", " +
                                "\nisCancelled ? " + future.isCancelled());
                        if (future.cause() != null) future.cause().printStackTrace();
                    }
                });
            } else {
                System.err.println("res == null");
            }
        } else {
            System.err.println("listener == null");
        }
    }
}
