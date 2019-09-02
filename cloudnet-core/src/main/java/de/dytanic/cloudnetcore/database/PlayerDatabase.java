/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.database;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.event.player.UpdatePlayerEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 01.07.2017.
 */
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
                                                        playerConnection,
                                                        new PermissionEntity(playerConnection.getUniqueId(),
                                                                             new HashMap<>(),
                                                                             null,
                                                                             null,
                                                                             new LinkedList<>()));
        database.insert(new DatabaseDocument(playerConnection.getUniqueId().toString()).append("offlinePlayer", offlinePlayer));
        return offlinePlayer;
    }

    public PlayerDatabase updatePlayer(OfflinePlayer offlinePlayer) {
        CloudNet.getLogger().debug("PlayerDatabase updatePlayer offlinePlayer null: " + (offlinePlayer == null));
        if (offlinePlayer == null) {
            return this;
        }
        Document document = database.getDocument(offlinePlayer.getUniqueId().toString());
        document.append("offlinePlayer", CloudPlayer.newOfflinePlayer(offlinePlayer));
        database.insert(document);
        CloudNet.getLogger().debug("PlayerDatabase updatePlayer call UpdatePlayerEvent");
        CloudNet.getInstance().getEventManager().callEvent(new UpdatePlayerEvent(offlinePlayer));
        return this;
    }

    public PlayerDatabase updateName(UUID uuid, String name) {
        Document document = database.getDocument(uuid.toString());
        OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", OfflinePlayer.TYPE);
        offlinePlayer.setName(name);
        document.append("offlinePlayer", offlinePlayer);
        database.insert(document);
        return this;
    }

    public boolean containsPlayer(UUID uuid) {
        return database.containsDoc(uuid.toString());
    }

    public PlayerDatabase updatePermissionEntity(UUID uuid, PermissionEntity permissionEntity) {
        Document document = database.getDocument(uuid.toString());
        OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", OfflinePlayer.TYPE);
        offlinePlayer.setPermissionEntity(permissionEntity);
        document.append("offlinePlayer", offlinePlayer);
        database.insert(document);
        return this;
    }

    public OfflinePlayer getPlayer(UUID uniqueId) {
        CloudNet.getLogger().debug("PlayerDatabase getPlayer uniqueId " + uniqueId);
        if (uniqueId == null) {
            return null;
        }
        Document document = database.getDocument(uniqueId.toString());
        CloudNet.getLogger().debug("PlayerDatabase getPlayer document null: " + (document == null));
        if (document == null) {
            return null;
        }
        CloudNet.getLogger().debug("PlayerDatabase getPlayer offlinePlayer contained: " + document.contains("offlinePlayer"));
        return document.getObject("offlinePlayer", OfflinePlayer.TYPE);
    }

    public Map<UUID, OfflinePlayer> getRegisteredPlayers() {
        database.loadDocuments();

        Map<UUID, OfflinePlayer> map = new HashMap<>();

        for (Document document : database.getDocs()) {
            OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", OfflinePlayer.TYPE);
            map.put(offlinePlayer.getUniqueId(), offlinePlayer);
        }

        return map;
    }

}
