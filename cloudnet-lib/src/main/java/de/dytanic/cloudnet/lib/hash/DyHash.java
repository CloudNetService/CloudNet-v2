/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Created by Tareko on 17.09.2017.
 */
public final class DyHash {

    private DyHash() {
    }

    public static String hashString(String encode) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(encode.getBytes(StandardCharsets.UTF_8));

            String string = new String(Base64.getMimeEncoder().encode(messageDigest.digest()), StandardCharsets.UTF_8);
            return string;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return encode;
    }
}
