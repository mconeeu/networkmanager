/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager;

import eu.mcone.networkmanager.api.ModuleHost;
import eu.mcone.networkmanager.console.ConsoleCommandExecutor;
import eu.mcone.networkmanager.console.ConsoleColor;
import eu.mcone.networkmanager.console.ConsoleReader;
import eu.mcone.networkmanager.console.Logger;
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

    private NetworkManager() {
        setInstance(this);
        manager = this;

        this.threadPool = Executors.newCachedThreadPool();

        this.consoleReader = new ConsoleReader();
        this.consoleReader.registerCommand(new ConsoleCommandExecutor());

        Logger.log("Enabel progress", ConsoleColor.CYAN + "Welcome to mc1-networkmanager. System is starting...");

        //TODO: Add MongoDB database
        moduleManager = new ModuleManager();
    }

    public static void main(String[] args) {
        new NetworkManager();
    }

    public void shutdown() {
        Logger.log("Shutdown progress", "Shutting down Modules...");
        moduleManager.disableModules();

        Logger.log("Shutdown progress", "Stop Netty server...");
        //TODO: Close Netty Server her

        Logger.log("Shutdown progress", "Close MongoDB connection...");
        //TODO: Close MongoDB Connection her

        Logger.log("Shutdown progress", "Good bye!");
        System.exit(0);
    }
}
