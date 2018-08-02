/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.console;

import eu.mcone.networkmanager.NetworkManager;
import eu.mcone.networkmanager.core.console.ConsoleColor;
import eu.mcone.networkmanager.core.console.Logger;
import eu.mcone.networkmanager.core.module.ModuleInfo;

import java.util.Map;

public class CommandExecutor implements eu.mcone.networkmanager.core.console.CommandExecutor {

    @Override
    public void onCommand(String cmd, String[] args) {
        if (args.length == 1) {
            if (cmd.equalsIgnoreCase("help")) {
                Logger.log(getClass(), "---------- [Help] ----------\n" +
                        "help > show this list\n" +
                        "info > returns some information about the networkmanager\n" +
                        "list modules > lists all registered modules\n" +
                        "start <module> > Start the module with the specified jar name\n" +
                        "stop <module> > Stop the module with the specified module name\n" +
                        "stop all > Stops all working modules\n" +
                        "shutdown > Stops all working modules and the Networkmanager");
            } else if (cmd.equalsIgnoreCase("info")) {
                Logger.log(getClass(), "---------- [Info] ----------\n" +
                        "Networkmanager info:\n" +
                        "loaded modules: " + NetworkManager.getInstance().getModuleManager().getModules().size());
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                if (args[1].equalsIgnoreCase("modules")) {
                    Logger.log(getClass(), "---------- [Module List] ----------\n");
                    for (Map.Entry<String, ModuleInfo> module : NetworkManager.getInstance().getModuleManager().getModules().entrySet()) {
                        Logger.log(getClass(), "Module: " + module.getValue().getModuleName() + isRunning(module.getValue().getRunning()));
                    }
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                NetworkManager.getInstance().getModuleManager().startModule(args[1]);
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (args[1].equalsIgnoreCase("all")) {
                    NetworkManager.getInstance().getModuleManager().stopModules();
                } else if (!args[1].equalsIgnoreCase("")) {
                    NetworkManager.getInstance().getModuleManager().stopModule(args[1]);
                } else {
                    Logger.log(getClass(), ConsoleColor.RED + "Please use /help");
                }
            } else if (args[0].equalsIgnoreCase("shutdown")){
                NetworkManager.getInstance().shutdown();
            } else {
                Logger.log(getClass(), ConsoleColor.RED + "Please use /help");
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
