/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.console.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class DefaultWriter extends Handler {

    public void printToSystem(String line, Level level) {
        if (level.intValue() >= Level.WARNING.intValue()) {
            System.err.print(line);
        } else {
            System.out.print(line);
        }
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().intValue() >= getLevel().intValue()) {
            printToSystem(getFormatter().format(record), record.getLevel());
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}

}
