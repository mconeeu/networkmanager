/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network;

import eu.mcone.networkmanager.api.network.pipeline.Decoder;
import eu.mcone.networkmanager.api.network.pipeline.Encoder;
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

    public ServerBootstrap(ChannelPacketHandler handler) {
        new Thread(() -> {
            EventLoopGroup bossGroup = EPOLL ? new EpollEventLoopGroup(4) : new NioEventLoopGroup(4);
            EventLoopGroup workerGroup = EPOLL ? new EpollEventLoopGroup(4) : new NioEventLoopGroup(4);

            try {
                io.netty.bootstrap.ServerBootstrap b = new io.netty.bootstrap.ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new Decoder(handler));
                                ch.pipeline().addLast(new Encoder(handler));
                                ch.pipeline().addLast(handler);
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture f = b.bind(PORT).sync().addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        log.info("Netty is listening @ Port:" + PORT);
                    } else {
                        log.info("Failed to bind @ Port:" + PORT);
                    }
                }).addListener(ChannelFutureListener.CLOSE_ON_FAILURE).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }).start();
    }

}
