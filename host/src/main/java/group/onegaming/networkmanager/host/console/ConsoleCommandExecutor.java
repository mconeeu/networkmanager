/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.host.console;

import group.onegaming.networkmanager.host.NetworkManager;
import group.onegaming.networkmanager.host.api.module.ModuleInfo;
import group.onegaming.networkmanager.host.api.module.NetworkModule;
import group.onegaming.networkmanager.core.api.console.CommandExecutor;
import group.onegaming.networkmanager.core.api.console.ConsoleColor;
import lombok.extern.java.Log;

@Log
public class ConsoleCommandExecutor implements CommandExecutor {

    @Override
    public void onCommand(String[] args) {
        if (args.length == 0) {
            log.info("Pleas use /wrapper help");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
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
            } else if (args[0].equalsIgnoreCase("info")) {
                log.info("---------- [Info] ----------\n" +
                        "Networkmanager info:\n" +
                        "loaded modules: " + NetworkManager.getManager().getModuleManager().getLoadedModules().size());
            } else if (args[0].equalsIgnoreCase("reload")) {
                NetworkManager.getManager().getModuleManager().reloadLoadedModules();
            } else if (args[0].equalsIgnoreCase("shutdown")) {
                NetworkManager.getInstance().shutdown();
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                if (args[1].equalsIgnoreCase("modules")) {
                    log.info("---------- [Loaded Modules List] ----------");
                    for (NetworkModule module : NetworkManager.getManager().getModuleManager().getLoadedModules()) {
                        log.info("Module: " + module.getModuleInfo().getName());
                    }
                    log.info(ConsoleColor.RED + "---------- [Unloaded Modules List] ----------");
                    for (ModuleInfo info : NetworkManager.getManager().getModuleManager().getUnloadedModules()) {
                        log.info(ConsoleColor.RED + "Module: " + info.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                NetworkManager.getInstance().getModuleManager().loadModule(args[1]);
                NetworkManager.getInstance().getModuleManager().enableLoadedModule(args[1]);
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (args[1].equalsIgnoreCase("all")) {
                    NetworkManager.getManager().getModuleManager().disableLoadedModules();
                } else {
                    NetworkManager.getInstance().getModuleManager().disableLoadedModule(args[0]);
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                NetworkManager.getInstance().getModuleManager().reloadLoadedModule(args[1]);
            }
        }
    }
}
