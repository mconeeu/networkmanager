/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.console;

import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.api.module.ModuleInfo;
import eu.mcone.networkmanager.core.api.console.CommandExecutor;
import eu.mcone.networkmanager.core.api.console.ConsoleColor;
import lombok.extern.java.Log;

@Log
public class ConsoleCommandExecutor implements CommandExecutor {

    @Override
    public void onCommand(String cmd, String[] args) {
        if (args.length == 0) {
            if (cmd.equalsIgnoreCase("help")) {
                log.info("---------- [Help] ----------\n" +
                        "help > show this list\n" +
                        "info > returns some information about the networkmanager\n" +
                        "list modules > lists all registered modules\n" +
                        "reload > reload all modules\n" +
                        "reload <module> > reload a specific module\n" +
                        "start <module> > Start the module with the specified jar name\n" +
                        "stop <module> > Stop the module with the specified module name\n" +
                        "stop all > Stops all working modules\n" +
                        "shutdown > Stops all working modules and the Networkmanager");
            } else if (cmd.equalsIgnoreCase("info")) {
                log.info("---------- [Info] ----------\n" +
                        "Networkmanager info:\n" +
                        "loaded modules: " + NetworkManager.getManager().getModuleManager().getModules().size());
            } else if (cmd.equalsIgnoreCase("reload")) {
                NetworkManager.getManager().getModuleManager().reload();
            }
        } else if (args.length == 1) {
            if (cmd.equalsIgnoreCase("list")) {
                if (args[0].equalsIgnoreCase("modules")) {
                    log.info("---------- [Module List] ----------");
                    for (ModuleInfo module : NetworkManager.getManager().getModuleManager().getModules()) {
                        log.info("Module: " + module.getName() + " " + isRunning(module.isRunning()));
                    }
                }
            } else if (cmd.equalsIgnoreCase("start")) {
                NetworkManager.getInstance().getModuleManager().loadModule(NetworkManager.getInstance().getModuleManager().getModuleInfo(args[0]));
            } else if (cmd.equalsIgnoreCase("stop")) {
                if (args[0].equalsIgnoreCase("all")) {
                    NetworkManager.getManager().getModuleManager().disableModules();
                } else if (NetworkManager.getInstance().getModuleManager().getModule(args[0]) != null) {
                    NetworkManager.getInstance().getModuleManager().disableModule(NetworkManager.getManager().getModuleManager().getModuleInfo(args[0]));
                    log.info(ConsoleColor.RED + "Please use /help");
                }
            } else if (cmd.equalsIgnoreCase("reload")){
                NetworkManager.getInstance().getModuleManager().reloadModule(NetworkManager.getManager().getModuleManager().getModuleInfo(args[0]));
            } else if (cmd.equalsIgnoreCase("shutdown")) {
                NetworkManager.getInstance().shutdown();
            } else {
                log.info(ConsoleColor.RED + "Please use /help");
            }
        }
    }

    private String isRunning(final Boolean isRunning) {
        if (isRunning) {
            return ConsoleColor.GREEN + "Running";
        } else {
            return ConsoleColor.RED + "Stopped";
        }
    }
}
