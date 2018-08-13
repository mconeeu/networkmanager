/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.console.log;

import lombok.Getter;
import sun.reflect.Reflection;

import java.io.IOException;
import java.util.logging.*;

public class MconeLogger {

    @Getter
    private FileHandler fileHandler = null;
    @Getter
    private Logger mconeLogger;

    public MconeLogger() {
        Formatter rootFormatter = new LoggerFormatter(false);
        Logger root = LogManager.getLogManager().getLogger("");
        root.setLevel(Level.ALL);

        for (Handler handler : root.getHandlers()) {
            root.removeHandler(handler);
        }

        try {
            fileHandler = new FileHandler("host.log", 1 << 24, 8, true);
            fileHandler.setFormatter(rootFormatter);
            fileHandler.setLevel(Level.ALL);
            root.addHandler(fileHandler);

            DefaultWriter defaultConsoleHandler = new DefaultWriter();
            defaultConsoleHandler.setLevel(Level.INFO);
            defaultConsoleHandler.setFormatter(rootFormatter);
            root.addHandler(defaultConsoleHandler);


            mconeLogger = Logger.getLogger("eu.mcone");
            mconeLogger.setUseParentHandlers(false);
            mconeLogger.setLevel(Level.ALL);
            mconeLogger.addHandler(fileHandler);

            ColouredWriter colouredConsoleHandler = new ColouredWriter();
            colouredConsoleHandler.setLevel(Level.ALL);
            colouredConsoleHandler.setFormatter(new LoggerFormatter(true));
            mconeLogger.addHandler(colouredConsoleHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getRootLogger() {
        return Logger.getLogger("");
    }

    public static Logger getLogger() {
        return Logger.getLogger(Reflection.getCallerClass().getName());
    }

}
