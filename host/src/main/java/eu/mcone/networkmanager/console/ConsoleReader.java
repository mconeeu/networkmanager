/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.console;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleReader {

    private Map<String, ConsoleCommandExecutor> executors;

    public ConsoleReader() {
        executors = new HashMap<>();

        new Thread(() -> {
            Scanner sc = new Scanner(System.in);

            while(sc.hasNext()) {
                String next;
                if ((next = sc.nextLine()) != null) {
                    Logger.log(getClass(), "new console input: '"+next+"'");
                    String[] line = next.split(" ");

                    for (HashMap.Entry<String, ConsoleCommandExecutor> e : executors.entrySet()) {
                        if (e.getKey() == null || e.getKey().equalsIgnoreCase(line[0])) {
                            e.getValue().onCommand(line[0], Arrays.copyOfRange(line, 1, line.length));
                        }
                    }
                }
            }
        }).start();
    }

    public void registerCommand(String cmd, ConsoleCommandExecutor executor) {
        executors.put(cmd, executor);
    }

    public void registerCommand(ConsoleCommandExecutor executor) {
        executors.put(null, executor);
    }

}
