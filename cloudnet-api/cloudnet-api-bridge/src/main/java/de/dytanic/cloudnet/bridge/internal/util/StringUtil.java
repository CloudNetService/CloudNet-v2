package de.dytanic.cloudnet.bridge.internal.util;

import java.util.Collection;

public class StringUtil {
    public static <T extends Collection<? super String>> T copyPartialMatches(final String token,
                                                                              final Iterable<String> originals,
                                                                              final T collection) throws UnsupportedOperationException,
        IllegalArgumentException {

        for (String string : originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }

    public static boolean startsWithIgnoreCase(final String string, final String prefix) throws IllegalArgumentException,
        NullPointerException {
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
