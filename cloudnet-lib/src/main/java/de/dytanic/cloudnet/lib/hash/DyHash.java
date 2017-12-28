/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.hash;

/**
 * Created by Tareko on 17.09.2017.
 */
public final class DyHash {

    private DyHash() {}

    public static String hashString(String encode)
    {
        StringBuilder stringBuilder = new StringBuilder();
        int length = encode.length();

        for(char c : encode.toCharArray())
        {
            byte b = (byte)c;
            char d = (char)b;
            int e = d;
            stringBuilder.append((long) (e + length * length));
        }
        return stringBuilder.toString();
    }
}