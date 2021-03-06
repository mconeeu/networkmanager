/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.client;

import group.onegaming.networkmanager.api.messaging.ClientMessageManager;
import group.onegaming.networkmanager.api.pipeline.Decoder;
import group.onegaming.networkmanager.api.pipeline.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class ClientBootstrap {

    private static final boolean EPOLL = Epoll.isAvailable();
    private static final int PORT = 4567;

    @Getter
    private final ClientMessageManager messageManager;
    @Getter
    private final ClientPacketManager packetManager;
    @Getter
    private final String host, resourceBundleName;

    final NetworkmanagerClient client;
    private int reconnectTrys;

    public ClientBootstrap(String host, String resourceBundleName, NetworkmanagerClient client) {
        this.messageManager = new ClientMessageManager();
        this.packetManager = new ClientPacketManager(this);
        this.host = host;
        this.resourceBundleName = resourceBundleName;
        this.client = client;
        this.reconnectTrys = 0;

        tryConnect();
    }

    private void tryConnect() {
        client.runAsync(() -> {
            EventLoopGroup workerGroup = EPOLL ? new EpollEventLoopGroup(4) : new NioEventLoopGroup(4);

            try {
                new Bootstrap()
                        .group(workerGroup)
                        .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ClientChannelPacketHandler packetHandler = new ClientChannelPacketHandler(packetManager, messageManager);

                                ch.pipeline().addLast(new Decoder(packetHandler));
                                ch.pipeline().addLast(new Encoder(packetHandler));
                                ch.pipeline().addLast(packetHandler);
                            }
                        })
                        .connect(host, PORT)
                        .sync()
                        .addListener((ChannelFutureListener) channelFuture -> {
                            if (channelFuture.isSuccess()) {
                                log.info("Netty is connected to "+host+" @ Port:" + PORT);
                                reconnectTrys = 0;
                            } else {
                                log.severe("Failed to connect to "+host+" @ Port:" + PORT);
                            }
                        })
                        .channel()
                        .closeFuture()
                        .sync();
            } catch (Exception e) {
                reconnectTrys++;

                System.err.println("Could not connect to Master. Reconnecting... ["+reconnectTrys+"]");
                System.err.println(e.getMessage());
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
    }

    void scheduleReconnect() {
        try {
            Thread.sleep(5000);
            tryConnect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
