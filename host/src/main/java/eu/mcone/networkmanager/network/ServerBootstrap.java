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
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.java.Log;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

@Log
public class ServerBootstrap {

    private static final int PORT = 4567;
    private static final boolean EPOLL = Epoll.isAvailable();

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private ChannelFuture masterChannel;
    private ChannelFuture webMasterChannel;

    public ServerBootstrap(PacketManager packetManager, WebRequestManager requestManager) {
        bossGroup = EPOLL ? new EpollEventLoopGroup(4) : new NioEventLoopGroup(4);
        workerGroup = EPOLL ? new EpollEventLoopGroup(4) : new NioEventLoopGroup(4);

        try {
            masterChannel = new io.netty.bootstrap.ServerBootstrap()
                    .group(bossGroup, workerGroup)
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

            webMasterChannel = new io.netty.bootstrap.ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws CertificateException, SSLException {
                            SelfSignedCertificate ssc = new SelfSignedCertificate();

                            ch.pipeline()
                                    /*.addLast(SslContextBuilder.forServer(
                                            ssc.certificate(),
                                            ssc.privateKey()).build().newHandler(ch.alloc())
                                    )*/
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(65536))
                                    .addLast(new ChannelWebRequestHandler(requestManager));
                        }
                    })
                    .bind(8080)
                    .sync()
                    .addListener((ChannelFutureListener) channelFuture -> {
                        if (channelFuture.isSuccess()) {
                            log.info("Netty Web is listening @ Port:" + PORT);
                        } else {
                            log.info("Netty Web failed to bind @ Port:" + PORT);
                        }
                    });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();

        try {
            masterChannel.channel().closeFuture().sync();
            webMasterChannel.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
