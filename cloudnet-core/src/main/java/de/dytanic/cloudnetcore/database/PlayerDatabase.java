/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.database;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.database.DatabaseUseable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.player.CloudPlayer;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tareko on 01.07.2017.
 */
public class PlayerDatabase extends DatabaseUseable {

    public PlayerDatabase(Database database)
    {
        super(database);
    }

    public PlayerDatabase registerPlayer(PlayerConnection playerConnection)
    {
        OfflinePlayer offlinePlayer = new OfflinePlayer(playerConnection.getUniqueId(), playerConnection.getName(), new Document(), System.currentTimeMillis(), System.currentTimeMillis(), playerConnection, null);
        database.insert(new DatabaseDocument(offlinePlayer.getUniqueId().toString()).append("offlinePlayer", offlinePlayer));
        return this;
    }

    public PlayerDatabase updatePlayer(OfflinePlayer offlinePlayer)
    {
        Document document = database.getDocument(offlinePlayer.getUniqueId().toString());
        document.append("offlinePlayer", CloudPlayer.newOfflinePlayer(offlinePlayer));
        database.insert(document);
        return this;
    }

    public PlayerDatabase updateName(UUID uuid, String name)
    {
        Document document = database.getDocument(uuid.toString());
        OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", new TypeToken<OfflinePlayer>(){}.getType());
        offlinePlayer.setName(name);
        database.insert(document);
        return this;
    }

    public boolean containsPlayer(UUID uuid)
    {
        return database.containsDoc(uuid.toString());
    }

    public PlayerDatabase updatePermissionEntity(UUID uuid, PermissionEntity permissionEntity)
    {
        Document document = database.getDocument(uuid.toString());
        OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", new TypeToken<OfflinePlayer>(){}.getType());
        offlinePlayer.setPermissionEntity(permissionEntity);
        database.insert(document);
        return this;
    }

    public OfflinePlayer getPlayer(UUID uniqueId)
    {
        if(uniqueId == null) return null;
        Document document = database.getDocument(uniqueId.toString());

        if(document == null) return null;

        return document.getObject("offlinePlayer", new TypeToken<OfflinePlayer>(){}.getType());
    }
}