/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.api.server;

import io.netty.channel.ChannelHandlerContext;

public interface ConnectionListener {

    void onChannelActive(String resourceBundleName, ChannelHandlerContext ctx);

    void onChannelUnregistered(ChannelHandlerContext ctx);

    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);

}
