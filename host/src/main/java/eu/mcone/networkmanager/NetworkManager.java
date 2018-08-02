/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager;

import eu.mcone.networkmanager.api.ModuleHost;
import eu.mcone.networkmanager.console.CommandExecutor;
import eu.mcone.networkmanager.core.console.ConsoleColor;
import eu.mcone.networkmanager.core.console.ConsoleReader;
import eu.mcone.networkmanager.core.console.Logger;
import eu.mcone.networkmanager.core.module.ModuleInfo;
import eu.mcone.networkmanager.file.ModuleFileReader;
import eu.mcone.networkmanager.manager.ModuleManager;
import lombok.Getter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class NetworkManager extends ModuleHost {

    public static final File HOME_DIR = new File(System.getProperty("user.dir"));
    @Getter
    private ModuleManager moduleManager;
    @Getter
    private ConsoleReader consoleReader;
    @Getter
    private ExecutorService threadPool;

    private NetworkManager() {
        setInstance(this);
        this.threadPool = Executors.newCachedThreadPool();
        consoleReader = new ConsoleReader();
        consoleReader.registerCommand(new CommandExecutor());

        Logger.log("Enabel progress", ConsoleColor.CYAN + "Welcome to mc1-networkmanager. System is starting...");
        //TODO: Add MariaDB database
        moduleManager = new ModuleManager();
        moduleManager.loadModules();
    }

    public static void main(String[] args) {
        new NetworkManager();
    }

    public void shutdown() {
        Logger.log("Shutdown progress", "Shutting down Modules...");
        moduleManager.stopModules();

        Logger.log("Shutdown progress", "Stop Netty server...");
        //TODO: Close Netty Server her
        Logger.log("Shutdown progress", "Close MariaDB connection...");
        //TODO: Close MariaDB Connection her

        Logger.log("Shutdown progress", "Good bye!");
        System.exit(0);
    }
}
