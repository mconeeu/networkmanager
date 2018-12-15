/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.network;

import eu.mcone.networkmanager.api.server.WebRequestListener;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.BAD_REQUEST;

public class ChannelWebRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final WebRequestManager manager;

    ChannelWebRequestHandler(WebRequestManager manager) {
        this.manager = manager;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        LinkedList<String> uri = new LinkedList<>(Arrays.asList(req.uri().split("/")));
        if (uri.size() > 0)
                uri.remove(0);

        WebRequestListener listener = null;
        listenerLoop:
        for (Map.Entry<String[], WebRequestListener> e : manager.getListeners().entrySet()) {
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

        FullHttpResponse res = null;
        if (listener != null) {
            res = listener.onRequest(req, uri);
        }

        res = res != null ? res : new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);

        if (res.content() != null)
            HttpUtil.setContentLength(res, res.content().readableBytes());

        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
