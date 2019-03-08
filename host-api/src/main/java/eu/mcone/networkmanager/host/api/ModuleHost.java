/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.api;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoDatabase;
import eu.mcone.networkmanager.api.messaging.HostClientMessageManager;
import eu.mcone.networkmanager.core.api.console.ConsoleReader;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.host.api.manager.EventManager;
import eu.mcone.networkmanager.host.api.manager.ModuleManager;
import eu.mcone.networkmanager.host.api.server.ServerPacketManager;
import lombok.Getter;

import java.util.concurrent.ExecutorService;

public abstract class ModuleHost {

    @Getter
    private static ModuleHost instance;

    public static void setInstance(ModuleHost instance) {
        if (ModuleHost.instance == null) {
            ModuleHost.instance = instance;
        } else {
            throw new IllegalStateException("ModuleHost instance was already set!");
        }
    }

    public abstract ExecutorService getThreadPool();

    public abstract ConsoleReader getConsoleReader();

    public abstract ModuleManager getModuleManager();

    public abstract EventManager getEventManager();

    public abstract MongoDatabase getMongoDatabase(Database database);

    public abstract ServerPacketManager getPacketManager();

    public abstract HostClientMessageManager getClientRequestManager();

    public abstract Gson getGson();

    public abstract JsonParser getJsonParser();

    public abstract void shutdown();

}
