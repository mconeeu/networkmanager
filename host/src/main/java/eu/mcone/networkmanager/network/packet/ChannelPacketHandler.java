/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network.packet;


import com.mongodb.client.MongoCollection;
import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.core.api.database.Database;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.java.Log;
import org.bson.Document;

@Log
public class ChannelPacketHandler extends SimpleChannelInboundHandler<Object> {

    //Collection um überprüfen zu können über der WebAPI Key mit dem WebAPI Key aus der Datenbank übereinstimmt.
    private MongoCollection<Document> WebAPICollection = NetworkManager.getManager().getMongoDatabase(Database.NETWORK).getCollection("WebAPI");

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("new channel from " + ctx.channel().remoteAddress().toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chc, Object o) {
        System.out.println("receive object from " + chc.channel().remoteAddress().toString());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
      log.info("unregister channel to " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
       log.severe("Netty Exception: " + cause.getMessage());
    }
}
