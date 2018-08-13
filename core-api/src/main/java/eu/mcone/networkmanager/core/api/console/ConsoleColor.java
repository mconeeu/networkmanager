/*
 * Copyright (c) 2012, md_5. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * The name of the author may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * You may not use the software for commercial software hosting services without
 * written permission from the author.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package eu.mcone.networkmanager.core.api.console;

import lombok.Getter;

public enum ConsoleColor {

    /**
     * Represents black.
     */
    BLACK('0', "black"),
    /**
     * Represents dark blue.
     */
    DARK_BLUE('1', "dark_blue"),
    /**
     * Represents dark green.
     */
    DARK_GREEN('2', "dark_green"),
    /**
     * Represents dark blue (aqua).
     */
    DARK_AQUA('3', "dark_aqua"),
    /**
     * Represents dark red.
     */
    DARK_RED('4', "dark_red"),
    /**
     * Represents dark purple.
     */
    DARK_PURPLE('5', "dark_purple"),
    /**
     * Represents gold.
     */
    GOLD('6', "gold"),
    /**
     * Represents gray.
     */
    GRAY('7', "gray"),
    /**
     * Represents dark gray.
     */
    DARK_GRAY('8', "dark_gray"),
    /**
     * Represents blue.
     */
    BLUE('9', "blue"),
    /**
     * Represents green.
     */
    GREEN('a', "green"),
    /**
     * Represents aqua.
     */
    AQUA('b', "aqua"),
    /**
     * Represents red.
     */
    RED('c', "red"),
    /**
     * Represents light purple.
     */
    LIGHT_PURPLE('d', "light_purple"),
    /**
     * Represents yellow.
     */
    YELLOW('e', "yellow"),
    /**
     * Represents white.
     */
    WHITE('f', "white"),
    /**
     * Represents magical characters that change around randomly.
     */
    MAGIC('k', "obfuscated"),
    /**
     * Makes the text bold.
     */
    BOLD('l', "bold"),
    /**
     * Makes a line appear through the text.
     */
    STRIKETHROUGH('m', "strikethrough"),
    /**
     * Makes the text appear underlined.
     */
    UNDERLINE('n', "underline"),
    /**
     * Makes the text italic.
     */
    ITALIC('o', "italic"),
    /**
     * Resets all previous chat colors or formats.
     */
    RESET('r', "reset");

    public static final char COLOR_CHAR = '\u00A7';

    private final String toString;
    @Getter
    private final String name;

    ConsoleColor(char code, String name) {
        this.name = name;
        this.toString = new String(new char[]{COLOR_CHAR, code});
    }

    @Override
    public String toString() {
        return toString;
    }

}
