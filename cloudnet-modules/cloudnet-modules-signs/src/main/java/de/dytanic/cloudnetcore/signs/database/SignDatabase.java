/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.signs.database;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 22.07.2017.
 */
public class SignDatabase extends DatabaseUsable {

    private static final Type MAP_STRING_SIGN_TYPE = TypeToken.getParameterized(Map.class, String.class, Sign.class).getType();

    public SignDatabase(Database database) {
        super(database);

        DatabaseDocument document = database.getDocument("signs");
        if (document == null) {
            database.insert(new DatabaseDocument("signs").append("signs", Collections.emptyMap()));
        }
    }

    public SignDatabase appendSign(Sign sign) {
        DatabaseDocument document = database.getDocument("signs");
        Map<String, Sign> map = document.getObject("signs", MAP_STRING_SIGN_TYPE);
        map.put(sign.getUniqueId().toString(), sign);
        document.append("signs", map);
        database.insert(document);
        return this;
    }

    public SignDatabase removeSign(UUID uniqueId) {
        DatabaseDocument document = database.getDocument("signs");
        Map<String, Sign> map = document.getObject("signs", MAP_STRING_SIGN_TYPE);
        map.remove(uniqueId.toString());
        document.append("signs", map);
        database.insert(document);
        return this;
    }

    public Map<UUID, Sign> loadAll() {
        DatabaseDocument document = database.getDocument("signs");
        Map<String, Sign> map = document.getObject("signs", MAP_STRING_SIGN_TYPE);
        Map<UUID, Sign> signs = new LinkedHashMap<>();
        for (final Map.Entry<String, Sign> entry : map.entrySet()) {
            signs.put(UUID.fromString(entry.getKey()), entry.getValue());
        }
        return signs;
    }

}
