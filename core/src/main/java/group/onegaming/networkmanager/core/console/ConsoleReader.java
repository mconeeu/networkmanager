/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.core.console;

import group.onegaming.networkmanager.core.api.console.CommandExecutor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleReader implements group.onegaming.networkmanager.core.api.console.ConsoleReader {

    private Map<String, Map<String, CommandExecutor>> executors;

    public ConsoleReader() {
        executors = new HashMap<>();

        new Thread(() -> {
            Scanner sc = new Scanner(System.in);

            String line;
            while (sc.hasNextLine() && (line = sc.nextLine()) != null) {
                String[] args = line.split(" ");

                for (HashMap.Entry<String, Map<String, CommandExecutor>> e : executors.entrySet()) {
                    if (e.getKey() != null) {
                        for (HashMap.Entry<String, CommandExecutor> commandEntry : e.getValue().entrySet()) {
                            if (commandEntry.getKey() != null && commandEntry.getKey().equalsIgnoreCase(args[0])) {
                                commandEntry.getValue().onCommand(Arrays.copyOfRange(args, 1, args.length));
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public void registerCommand(String moduleName, String cmd, CommandExecutor executor) {
        if (executors.containsKey(moduleName)) {
            executors.get(moduleName).put(cmd, executor);
        } else {
            executors.put(moduleName, new HashMap<String, CommandExecutor>() {{
                put(cmd, executor);
            }});
        }
    }
}
