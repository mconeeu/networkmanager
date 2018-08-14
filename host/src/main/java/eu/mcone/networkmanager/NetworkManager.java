/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager;

import com.google.gson.Gson;
import eu.mcone.networkmanager.api.ModuleHost;
import eu.mcone.networkmanager.console.ConsoleCommandExecutor;
import eu.mcone.networkmanager.core.api.console.ConsoleColor;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.api.database.MongoDatabase;
import eu.mcone.networkmanager.core.console.ConsoleReader;
import eu.mcone.networkmanager.core.console.log.MconeLogger;
import eu.mcone.networkmanager.core.database.MongoConnection;
import eu.mcone.networkmanager.module.ModuleManager;
import eu.mcone.networkmanager.network.ServerBootstrap;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
public class NetworkManager extends ModuleHost {

    @Getter
    private static NetworkManager manager;
    public static final File HOME_DIR = new File(System.getProperty("user.dir"));

    @Getter
    private Gson gson;
    @Getter
    private MconeLogger mconeLogger;
    @Getter
    private ServerBootstrap serverBootstrap;
    @Getter
    private ModuleManager moduleManager;
    @Getter
    private ConsoleReader consoleReader;
    @Getter
    private ExecutorService threadPool;
    @Getter
    private MongoConnection mongoConnection;

    private NetworkManager() {
        setInstance(this);
        manager = this;

        gson = new Gson();
        mconeLogger = new MconeLogger();
        threadPool = Executors.newCachedThreadPool();
        consoleReader = new ConsoleReader();
        consoleReader.registerCommand(new ConsoleCommandExecutor());

        log.info("Enable progress - " + ConsoleColor.AQUA + "Welcome to mc1-networkmanager. System is starting...");

        log.info("Enable progress - " + ConsoleColor.GREEN + "Start connection to MongoDatabase...");
        mongoConnection = new MongoConnection("db.mcone.eu", "admin", "T6KIq8gjmmF1k7futx0cJiJinQXgfguYXruds1dFx1LF5IsVPQjuDTnlI1zltpD9", "admin", 27017);
        mongoConnection.connect();

        log.info("Enable progress - " + ConsoleColor.GREEN + "Start server bootstrap...");
        serverBootstrap = new ServerBootstrap(40000);

        log.info("Enable progress - " + ConsoleColor.GREEN + "Start moduleManager...");
        moduleManager = new ModuleManager();

        log.finest(ConsoleColor.GREEN + "READY!");
    }

    public static void main(String[] args) {
        new NetworkManager();
    }

    @Override
    public MongoDatabase getMongoDatabase(Database database) {
        return mongoConnection.getDatabase(database);
    }


    public void shutdown() {
        log.info("Shutdown progress - Shutting down Modules...");
        moduleManager.disableModules();

        log.info("Shutdown progress - Closing connection to database...");
        mongoConnection.disconnect();

        log.info("Shutdown progress - Good bye!");
        System.exit(0);
    }
}
