/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import eu.mcone.networkmanager.api.ModuleHost;
import eu.mcone.networkmanager.console.ConsoleCommandExecutor;
import eu.mcone.networkmanager.core.api.console.ConsoleColor;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.api.database.MongoDatabase;
import eu.mcone.networkmanager.core.console.ConsoleReader;
import eu.mcone.networkmanager.core.console.log.MconeLogger;
import eu.mcone.networkmanager.core.database.MongoConnection;
import eu.mcone.networkmanager.module.EventManager;
import eu.mcone.networkmanager.module.ModuleManager;
import eu.mcone.networkmanager.network.PacketManager;
import eu.mcone.networkmanager.network.ServerBootstrap;
import eu.mcone.networkmanager.network.ClientRequestManager;
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
    private JsonParser jsonParser;
    @Getter
    private MconeLogger mconeLogger;
    @Getter
    private ConsoleReader consoleReader;

    @Getter
    private ExecutorService threadPool;
    @Getter
    private MongoConnection mongoConnection;
    @Getter
    private ModuleManager moduleManager;
    @Getter
    private EventManager eventManager;

    private ServerBootstrap serverBootstrap;
    @Getter
    private PacketManager packetManager;
    @Getter
    private ClientRequestManager clientRequestManager;

    private NetworkManager() {
        setInstance(this);
        manager = this;

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        gson = new Gson();
        jsonParser = new JsonParser();
        mconeLogger = new MconeLogger();
        threadPool = Executors.newCachedThreadPool();
        consoleReader = new ConsoleReader();
        consoleReader.registerCommand(new ConsoleCommandExecutor());
        eventManager = new EventManager();

        log.info("Enable progress - " + ConsoleColor.AQUA + "Welcome to mc1-networkmanager. System is starting...");

        clientRequestManager = new ClientRequestManager();
        packetManager = new PacketManager(clientRequestManager);

        log.info("Enable progress - " + ConsoleColor.GREEN + "Start moduleManager...");
        moduleManager = new ModuleManager();
        moduleManager.loadModules();

        log.info("Enable progress - " + ConsoleColor.GREEN + "Start server bootstrap...");
        serverBootstrap = new ServerBootstrap(packetManager);

        log.info("Enable progress - " + ConsoleColor.GREEN + "Start connection to MongoDatabase...");
        mongoConnection = new MongoConnection(
                "db.mcone.eu",
                "admin",
                "T6KIq8gjmmF1k7futx0cJiJinQXgfguYXruds1dFx1LF5IsVPQjuDTnlI1zltpD9",
                "admin",
                27017
        ).connect();

        moduleManager.enableLoadedModules();

        log.info(ConsoleColor.GREEN + "The network-manager is now ready!");
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
        moduleManager.disableLoadedModules();

        log.info("Shutdown progress - Closing netty server...");
        serverBootstrap.shutdown();

        log.info("Shutdown progress - Closing connection to database...");
        mongoConnection.disconnect();

        log.info("Shutdown progress - Good bye!");
        System.exit(0);
    }
}
