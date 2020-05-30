/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.core.console.log;

import group.onegaming.networkmanager.core.api.console.ConsoleColor;
import org.fusesource.jansi.Ansi;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

public class ColouredWriter extends DefaultWriter {

    private final static Map<ConsoleColor, String> REPLACEMENTS = new EnumMap<ConsoleColor, String>(ConsoleColor.class) {{
        put(ConsoleColor.BLACK, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
        put(ConsoleColor.DARK_BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
        put(ConsoleColor.DARK_GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
        put(ConsoleColor.DARK_AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
        put(ConsoleColor.DARK_RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
        put(ConsoleColor.DARK_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
        put(ConsoleColor.GOLD, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
        put(ConsoleColor.GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
        put(ConsoleColor.DARK_GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
        put(ConsoleColor.BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
        put(ConsoleColor.GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
        put(ConsoleColor.AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
        put(ConsoleColor.RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
        put(ConsoleColor.LIGHT_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
        put(ConsoleColor.YELLOW, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
        put(ConsoleColor.WHITE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
        put(ConsoleColor.MAGIC, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
        put(ConsoleColor.BOLD, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
        put(ConsoleColor.STRIKETHROUGH, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
        put(ConsoleColor.UNDERLINE, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
        put(ConsoleColor.ITALIC, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());
        put(ConsoleColor.RESET, Ansi.ansi().a(Ansi.Attribute.RESET).toString());
    }};

    @Override
    public void printToSystem(String s, Level level) {
        for (ConsoleColor color : ConsoleColor.values()) {
            s = s.replaceAll("(?i)" + color.toString(), REPLACEMENTS.get(color));
        }

        if (level.intValue() >= Level.WARNING.intValue()) {
            System.err.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + "\r" + s + Ansi.ansi().reset().toString());
        } else {
            System.out.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + "\r" + s + Ansi.ansi().reset().toString());
        }
    }

}
