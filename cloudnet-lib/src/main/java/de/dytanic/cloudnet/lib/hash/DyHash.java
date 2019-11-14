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

    /**
     * (insecurely, but quickly) hashes the given String using SHA-256.
     *
     * @param string the string to hash/encode
     *
     * @return the hashed string in Base64
     */
    public static String hashString(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(string.getBytes(StandardCharsets.UTF_8));

            return Base64.getMimeEncoder().encodeToString(messageDigest.digest());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return string;
    }
}
