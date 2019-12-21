/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.mobs.database;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 01.09.2017.
 */
public class MobDatabase extends DatabaseUsable {

    public static final Type MAP_UUID_SERVERMOB_TYPE = TypeToken.getParameterized(Map.class, UUID.class, ServerMob.TYPE).getType();

    public MobDatabase(Database database) {
        super(database);
        DatabaseDocument document = database.getDocument("server_selector_mobs");
        if (document == null) {
            document = new DatabaseDocument("server_selector_mobs")
                .append("mobs", new Document());
        }
        database.insert(document);
    }

    public void append(ServerMob serverMob) {
        final DatabaseDocument mobDocument = database.getDocument("server_selector_mobs");
        Document document = mobDocument
            .getDocument("mobs")
            .append(serverMob.getUniqueId().toString(), serverMob);
        mobDocument.append("server_selector_mobs", document);
        database.insert(mobDocument);
    }

    public void remove(ServerMob serverMob) {
        final DatabaseDocument mobDocument = database.getDocument("server_selector_mobs");
        Document document = mobDocument
            .getDocument("mobs")
            .remove(serverMob.getUniqueId().toString());
        mobDocument.append("server_selector_mobs", document);
        database.insert(mobDocument);
    }

    public Map<UUID, ServerMob> loadAll() {
        boolean injectable = false;
        Map<UUID, ServerMob> mobMap = database.getDocument("server_selector_mobs").getObject("mobs", MAP_UUID_SERVERMOB_TYPE);

        for (ServerMob serverMob : mobMap.values()) {
            if (serverMob.getItemId() == null) {
                serverMob.setItemId(138);
                injectable = true;
            }
            if (serverMob.getAutoJoin() == null) {
                serverMob.setAutoJoin(false);
                injectable = true;
            }
        }

        if (injectable) {
            DatabaseDocument document = database.getDocument("server_selector_mobs");
            document.append("mobs", mobMap);
            database.insert(document);
        }

        return mobMap;
    }

}
