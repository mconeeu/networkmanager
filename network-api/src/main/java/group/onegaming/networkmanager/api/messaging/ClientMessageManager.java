/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.api.messaging;

import group.onegaming.networkmanager.api.messaging.request.ClientMessageRequestListener;
import group.onegaming.networkmanager.api.packet.ClientMessageRequestPacket;
import group.onegaming.networkmanager.api.packet.ClientMessageResponsePacket;
import group.onegaming.networkmanager.api.pipeline.FutureListeners;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.*;

@Log
public class ClientMessageManager implements HostClientMessageManager {

    @Getter
    private final Map<String, ClientMessageRequestListener> listeners;

    public ClientMessageManager() {
        this.listeners = new HashMap<>();

        registerClientMessageListener("networkmanager.status", packet ->
                new ClientMessageResponsePacket(packet.getRequestUuid(), HttpResponseStatus.OK)
        );
    }

    @Override
    public void registerClientMessageListener(String uri, ClientMessageRequestListener listener) throws IllegalStateException {
        uri = uri.toLowerCase(Locale.ROOT);
        if (listeners.containsKey(uri)) {
            log.info("Overriding existing ClientMessageListener with uri: " + uri);
        }

        listeners.put(uri, listener);
    }

    public void onPacketReceive(ClientMessageRequestPacket packet, ChannelHandlerContext ctx) {
        ClientMessageRequestListener listener = null;

        String[] uriSplit = packet.getUri().replace('.', '-').split("-");
        StringBuilder uriConstructor = new StringBuilder();
        for (int i = 0; i < uriSplit.length - 1; i++) {
            uriConstructor.append(uriConstructor.toString().equals("") ? "" : ".").append(uriSplit[i]);
            listener = listeners.getOrDefault(uriConstructor + ".*", null);

            if (listener != null) break;
        }

        if (listener != null) {
            ClientMessageResponsePacket res = listener.onClientRequest(packet);

            if (res != null) {
                ctx.writeAndFlush(res).addListener(FutureListeners.FUTURE_LISTENER);
            }
        }
    }

}
