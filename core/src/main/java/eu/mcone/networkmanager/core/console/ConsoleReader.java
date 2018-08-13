/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.console;

import eu.mcone.networkmanager.core.api.console.CommandExecutor;
import org.jline.reader.LineReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleReader implements eu.mcone.networkmanager.core.api.console.ConsoleReader {

    private Map<String, CommandExecutor> executors;
    private LineReader reader;

    public ConsoleReader() {
        executors = new HashMap<>();
        //reader = LineReaderBuilder.builder().build();

        new Thread(() -> {
            Scanner sc = new Scanner(System.in);

            String line;
            while(sc.hasNext() && (line = sc.next()) != null) {
                String[] args = line.split(" ");

                for (HashMap.Entry<String, CommandExecutor> e : executors.entrySet()) {
                    if (e.getKey() == null || e.getKey().equalsIgnoreCase(args[0])) {
                        e.getValue().onCommand(args[0], Arrays.copyOfRange(args, 1, args.length));
                    }
                }
            }
        }).start();
    }

    public void registerCommand(String cmd, CommandExecutor executor) {
        executors.put(cmd, executor);
    }

    public void registerCommand(CommandExecutor executor) {
        executors.put(null, executor);
    }

}
