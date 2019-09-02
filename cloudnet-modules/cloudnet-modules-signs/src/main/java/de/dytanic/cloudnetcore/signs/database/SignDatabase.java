/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.signs.database;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by Tareko on 22.07.2017.
 */
public class SignDatabase extends DatabaseUsable {

    public SignDatabase(Database database) {
        super(database);

        Document document = database.getDocument("signs");
        if (document == null) {
            database.insert(new DatabaseDocument("signs").append("signs", new Document()));
        }
    }

    public SignDatabase appendSign(Sign sign) {
        Document x = database.getDocument("signs");
        Document document = x.getDocument("signs");
        document.append(sign.getUniqueId().toString(), sign);
        database.insert(document);
        return this;
    }

    public SignDatabase removeSign(UUID uniqueId) {
        Document x = database.getDocument("signs");
        Document document = x.getDocument("signs");
        document.remove(uniqueId.toString());
        database.insert(document);
        return this;
    }

    public java.util.Map<UUID, Sign> loadAll() {
        Document x = database.getDocument("signs");
        Document document = x.getDocument("signs");
        Type typeToken = new TypeToken<Sign>() {}.getType();
        java.util.Map<UUID, Sign> signs = new LinkedHashMap<>();
        for (String key : document.keys()) {
            signs.put(UUID.fromString(key), document.getObject(key, typeToken));
        }
        return signs;
    }

}
