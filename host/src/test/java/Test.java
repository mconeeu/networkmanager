/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

import com.google.gson.JsonObject;
import group.onegaming.networkmanager.api.packet.Packet;
import group.onegaming.networkmanager.api.packet.PacketRegisterPacketClient;
import group.onegaming.networkmanager.api.packet.interfaces.PacketResolver;
import group.onegaming.networkmanager.api.pipeline.Encoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        JsonObject o = new JsonObject();
        o.addProperty("test", false);

        System.out.println(o.toString());

        new ServerBootstrap()
                .group(
                        new NioEventLoopGroup(4),
                        new NioEventLoopGroup(4)
                )
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        System.out.println("new connection from "+ch.remoteAddress().getHostString());
                    }
                })
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ByteToMessageDecoder() {
                            @Override
                            protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
                                System.out.println("buf size: "+buf.readableBytes());
                                System.out.println(buf.readInt());

                                int size = buf.readInt();
                                System.out.println("packet size: "+size);
                                System.out.println("reader index @ begin of packet: "+ buf.readerIndex());

                                byte[] msg = new byte[buf.readInt()];
                                buf.readBytes(msg);

                                try {
                                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(msg));
                                    System.out.println(in.readUTF());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                PacketRegisterPacketClient packet = new PacketRegisterPacketClient(new HashMap<Class<? extends Packet>, Integer>(){{
                                    put(PacketRegisterPacketClient.class, 7);
                                }});
                                ByteBuf buf1 = Unpooled.buffer();
                                new Encoder(new PacketResolver() {
                                    @Override
                                    public Class<? extends Packet> getPacketById(int id) {
                                        return null;
                                    }

                                    @Override
                                    public int getPacketId(Class<? extends Packet> packet) {
                                        return 7;
                                    }
                                }).encode(ctx, packet, buf1);

                                ctx.writeAndFlush(buf1);
                                System.out.println("ENDE");
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                System.err.println(cause.getMessage());
                                cause.printStackTrace();
                            }
                        });
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .bind(4444)
                .sync()
                .addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        System.out.println("Netty is listening @ Port:" + 4444);
                    } else {
                        System.out.println("Netty failed to bind @ Port:" + 4444);
                    }
                })
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                .sync();
    }

}
