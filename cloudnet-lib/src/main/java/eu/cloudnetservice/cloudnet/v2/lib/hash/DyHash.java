/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.lib.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

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
