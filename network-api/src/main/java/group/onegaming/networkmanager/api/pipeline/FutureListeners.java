/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.api.pipeline;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.java.Log;

@Log
public final class FutureListeners {

    public final static GenericFutureListener<Future<? super Void>> FUTURE_LISTENER = future -> {
        if (!future.isSuccess() || future.isCancelled()) {
            log.severe("Netty Flush Operation failed:" +
                    "\nisDone ? " + future.isDone() + ", " +
                    "\nisSuccess ? " + future.isSuccess() + ", " +
                    "\ncause : " + future.cause() + ", " +
                    "\nisCancelled ? " + future.isCancelled());
            if (future.cause() != null) future.cause().printStackTrace();
        }
    };

}
