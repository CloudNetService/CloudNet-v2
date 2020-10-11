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

package eu.cloudnetservice.cloudnet.v2.lib.player;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.UUID;

public class PlayerConnection {

    public static final Type TYPE = TypeToken.get(PlayerConnection.class).getType();

    private final UUID uniqueId;
    private final String name;
    private final int version;
    private final String host;
    private final int port;
    private final boolean onlineMode;
    private final boolean legacy;

    public PlayerConnection(UUID uniqueId, String name, int version, String host, int port, boolean onlineMode, boolean legacy) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.version = version;
        this.host = host;
        this.port = port;
        this.onlineMode = onlineMode;
        this.legacy = legacy;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public int getVersion() {
        return version;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public boolean isLegacy() {
        return legacy;
    }
}
