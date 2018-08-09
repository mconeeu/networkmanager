/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager;

import eu.mcone.networkmanager.api.ModuleHost;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.api.database.MongoDBManager;
import eu.mcone.networkmanager.core.console.ConsoleColor;
import eu.mcone.networkmanager.console.ConsoleCommandExecutor;
import eu.mcone.networkmanager.core.console.ConsoleReader;
import eu.mcone.networkmanager.core.console.Logger;
import eu.mcone.networkmanager.core.database.MongoConnection;
import eu.mcone.networkmanager.manager.ModuleManager;
import lombok.Getter;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager extends ModuleHost {

    @Getter
    private static NetworkManager manager;
    public static final File HOME_DIR = new File(System.getProperty("user.dir"));

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

        threadPool = Executors.newCachedThreadPool();

        consoleReader = new ConsoleReader();
        consoleReader.registerCommand(new ConsoleCommandExecutor());

        Logger.log("Enable progress", ConsoleColor.CYAN + "Welcome to mc1-networkmanager. System is starting...");

        Logger.log(getClass(), ConsoleColor.GREEN + "Start connection to MongoDatabase...");
        mongoConnection = new MongoConnection("db.mcone.eu", "networkmanager", "", "networkmanager", 27017);
        mongoConnection.connect();

        Logger.log(getClass(), ConsoleColor.GREEN + "Start moduleManager...");
        moduleManager = new ModuleManager();
    }

    public static void main(String[] args) {
        new NetworkManager();
    }

    @Override
    public MongoDBManager getMongoDatabase(Database database) {
        return mongoConnection.getDatabase(database);
    }


    public void shutdown() {
        Logger.log("Shutdown progress", "Shutting down Modules...");
        moduleManager.disableModules();

        Logger.log("Shutdown progress", "Stop Netty server...");
        //TODO: Close Netty Server her

        Logger.log("Shutdown progress", "Close connection to database...");
        mongoConnection.disconnect();

        Logger.log("Shutdown progress", "Good bye!");
        System.exit(0);
    }
}
