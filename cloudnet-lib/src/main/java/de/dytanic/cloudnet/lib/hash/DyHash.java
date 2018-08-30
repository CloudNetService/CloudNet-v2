/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Created by Tareko on 17.09.2017.
 */
public final class DyHash {

    private DyHash()
    {
    }

    public static String hashString(String encode)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(encode.getBytes(StandardCharsets.UTF_8));

            String string = bytesToHex(messageDigest.digest());
            return string;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return encode;
    }
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}