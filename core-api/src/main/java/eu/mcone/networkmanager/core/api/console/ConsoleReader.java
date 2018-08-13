/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.api.console;

public interface ConsoleReader {

    void registerCommand(String cmd, CommandExecutor executor);

    void registerCommand(CommandExecutor executor);

}
