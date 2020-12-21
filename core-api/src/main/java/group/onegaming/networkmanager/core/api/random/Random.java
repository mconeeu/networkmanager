/*
 * Copyright (c) 2017 - 2019 Rufus Maiwald, Dominik Lippl and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package group.onegaming.networkmanager.core.api.random;

import java.security.SecureRandom;
import java.util.Locale;

public final class Random extends SecureRandom {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ", LOWER = UPPER.toLowerCase(Locale.ROOT), DIGITS = "0123456789", ALPHANUM = UPPER + LOWER + DIGITS;
    private final char[] symbols, buf;

    public Random(int length) {
        this(length, ALPHANUM);
    }

    public Random(int length, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();

        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * generates a random String
     * @return random String
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[nextInt(symbols.length)];
        return new String(buf);
    }

    /**
     * creates a random Integer
     * @param min minimum int inclusive
     * @param max maximum int exclusive
     * @return random int
     */
    public int nextInt(int min, int max) {
        return nextInt(max) + min;
    }

}
