/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.network;

import eu.mcone.networkmanager.api.pipeline.Decoder;
import eu.mcone.networkmanager.api.pipeline.Encoder;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.java.Log;

@Log
public class ServerBootstrap {

    private static final int PORT = 4567;
    private static final boolean EPOLL = Epoll.isAvailable();

    private final EventLoopGroup eventLoop;
    private ChannelFuture masterChannel;

    public ServerBootstrap(PacketManager packetManager) {
        eventLoop = EPOLL ? new EpollEventLoopGroup(10) : new NioEventLoopGroup(10);

        try {
            masterChannel = new io.netty.bootstrap.ServerBootstrap()
                    .group(eventLoop)
                    .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPacketHandler handler = new ChannelPacketHandler(packetManager);

                            ch.pipeline()
                                    .addLast("decoder", new Decoder(handler))
                                    .addLast("encoder", new Encoder(handler))
                                    .addLast("packetHandler", handler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(PORT)
                    .sync()
                    .addListener((ChannelFutureListener) channelFuture -> {
                        if (channelFuture.isSuccess()) {
                            log.info("Netty is listening @ Port:" + PORT);
                        } else {
                            log.info("Netty failed to bind @ Port:" + PORT);
                        }
                    })
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        eventLoop.shutdownGracefully();

        try {
            masterChannel.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
