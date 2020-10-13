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

package eu.cloudnetservice.cloudnet.v2.master.database;

import eu.cloudnetservice.cloudnet.v2.database.DatabaseUsable;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.player.OfflinePlayer;
import eu.cloudnetservice.cloudnet.v2.lib.player.PlayerConnection;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.api.event.player.UpdatePlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDatabase extends DatabaseUsable {

    public PlayerDatabase(Database database) {
        super(database);
    }

    public OfflinePlayer registerPlayer(PlayerConnection playerConnection) {
        OfflinePlayer offlinePlayer = new OfflinePlayer(playerConnection.getUniqueId(),
                                                        playerConnection.getName(),
                                                        new Document(),
                                                        System.currentTimeMillis(),
                                                        System.currentTimeMillis(),
                                                        playerConnection);
        database.insert(new DatabaseDocument(playerConnection.getUniqueId().toString()).append("offlinePlayer", offlinePlayer));
        return offlinePlayer;
    }

    public PlayerDatabase updatePlayer(OfflinePlayer offlinePlayer) {
        CloudNet.getLogger().finest("PlayerDatabase updatePlayer offlinePlayer null: " + (offlinePlayer == null));
        if (offlinePlayer == null) {
            return this;
        }
        DatabaseDocument document = database.getDocument(offlinePlayer.getUniqueId().toString());
        document.append("offlinePlayer", CloudPlayer.newOfflinePlayer(offlinePlayer));
        database.insert(document);
        CloudNet.getLogger().finest("PlayerDatabase updatePlayer call UpdatePlayerEvent");
        CloudNet.getInstance().getEventManager().callEvent(new UpdatePlayerEvent(offlinePlayer));
        return this;
    }

    public PlayerDatabase updateName(UUID uuid, String name) {
        DatabaseDocument document = database.getDocument(uuid.toString());
        OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", OfflinePlayer.TYPE);
        offlinePlayer.setName(name);
        document.append("offlinePlayer", offlinePlayer);
        database.insert(document);
        return this;
    }

    public boolean containsPlayer(UUID uuid) {
        return database.contains(uuid.toString());
    }

    public OfflinePlayer getPlayer(UUID uniqueId) {
        CloudNet.getLogger().finest("PlayerDatabase getPlayer uniqueId " + uniqueId);
        if (uniqueId == null) {
            return null;
        }
        DatabaseDocument document = database.getDocument(uniqueId.toString());
        CloudNet.getLogger().finest("PlayerDatabase getPlayer document null: " + (document == null));
        if (document == null) {
            return null;
        }
        CloudNet.getLogger().finest("PlayerDatabase getPlayer offlinePlayer contained: " + document.contains("offlinePlayer"));
        return document.getObject("offlinePlayer", OfflinePlayer.TYPE);
    }

    public Map<UUID, OfflinePlayer> getRegisteredPlayers() {
        database.loadDocuments();

        Map<UUID, OfflinePlayer> map = new HashMap<>();

        for (DatabaseDocument document : database.getDocuments().values()) {
            final OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", OfflinePlayer.TYPE);
            map.put(offlinePlayer.getUniqueId(), offlinePlayer);
        }

        return map;
    }

}
