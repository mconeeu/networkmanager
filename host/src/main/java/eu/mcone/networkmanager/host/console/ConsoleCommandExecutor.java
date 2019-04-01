/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.host.console;

import eu.mcone.networkmanager.host.NetworkManager;
import eu.mcone.networkmanager.host.api.module.ModuleInfo;
import eu.mcone.networkmanager.host.api.module.NetworkModule;
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
                        "loaded modules: " + NetworkManager.getManager().getModuleManager().getLoadedModules().size());
            } else if (cmd.equalsIgnoreCase("reload")) {
                NetworkManager.getManager().getModuleManager().reloadLoadedModules();
            }
        } else if (args.length == 1) {
            if (cmd.equalsIgnoreCase("list")) {
                if (args[0].equalsIgnoreCase("modules")) {
                    log.info("---------- [Loaded Modules List] ----------");
                    for (NetworkModule module : NetworkManager.getManager().getModuleManager().getLoadedModules()) {
                        log.info("Module: " + module.getModuleInfo().getName());
                    }
                    log.info(ConsoleColor.RED+"---------- [Unloaded Modules List] ----------");
                    for (ModuleInfo info : NetworkManager.getManager().getModuleManager().getUnloadedModules()) {
                        log.info(ConsoleColor.RED+"Module: " + info.getName());
                    }
                }
            } else if (cmd.equalsIgnoreCase("start")) {
                NetworkManager.getInstance().getModuleManager().loadModule(args[0]);
                NetworkManager.getInstance().getModuleManager().enableLoadedModule(args[0]);
            } else if (cmd.equalsIgnoreCase("stop")) {
                if (args[0].equalsIgnoreCase("all")) {
                    NetworkManager.getManager().getModuleManager().disableLoadedModules();
                } else {
                    NetworkManager.getInstance().getModuleManager().disableLoadedModule(args[0]);
                }
            } else if (cmd.equalsIgnoreCase("reload")){
                NetworkManager.getInstance().getModuleManager().reloadLoadedModule(args[0]);
            } else if (cmd.equalsIgnoreCase("shutdown")) {
                NetworkManager.getInstance().shutdown();
            }
        }
    }

}
