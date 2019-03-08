/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.client;

import io.netty.channel.ChannelHandlerContext;

public interface NetworkmanagerClient {

    void runAsync(Runnable runnable);

    void onChannelActive(ChannelHandlerContext ctx);

    void onChannelUnregistered(ChannelHandlerContext ctx);

}
