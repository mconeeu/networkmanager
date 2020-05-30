/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.core.console.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggerFormatter extends Formatter {

    private final static DateFormat DATE = new SimpleDateFormat(System.getProperty("net.md_5.bungee.log-date-format", "HH:mm:ss"));

    private final boolean addClassName;

    LoggerFormatter(boolean addClassName) {
        this.addClassName = addClassName;
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder formatted = new StringBuilder();
        String[] loggerName = record.getLoggerName().replace(".", "-").split("-");

        formatted.append(DATE.format(record.getMillis()))
                .append(" [")
                .append(record.getLevel().getLocalizedName())
                .append("] ");

        if (addClassName && !record.getLoggerName().contains("noClassName") && loggerName.length > 0) {
            formatted.append("[")
                    .append(loggerName[loggerName.length - 1])
                    .append("] ");
        }

        formatted.append(formatMessage(record))
                .append('\n');

        if (record.getThrown() != null) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            formatted.append(writer);
        }

        return formatted.toString();
    }

}
