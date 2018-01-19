/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.database;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.database.DatabaseUseable;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.database.DatabaseImpl;

import java.util.UUID;

/**
 * Created by Tareko on 20.08.2017.
 */
public class NameToUUIDDatabase extends DatabaseUseable {

    public NameToUUIDDatabase(Database database)
    {
        super(database);
    }

    public DatabaseImpl a()
    {
        return ((DatabaseImpl) database);
    }

    public void append(MultiValue<String, UUID> values)
    {
        if (!a().containsDoc(values.getFirst()))
            database.insert(new DatabaseDocument(values.getFirst()).append("uniqueId", values.getSecond()));
        else
            database.insert(database.getDocument(values.getFirst()).append("uniqueId", values.getSecond()));

        if (!a().containsDoc(values.getSecond().toString()))
            database.insert(new DatabaseDocument(values.getSecond().toString()).append("name", values.getFirst()));
        else
            database.insert(database.getDocument(values.getSecond().toString()).append("name", values.getFirst()));
    }

    public void replace(MultiValue<UUID, String> replacer)
    {
        Document document = database.getDocument(replacer.getFirst().toString());
        document.append("name", replacer.getSecond());
        database.insert(document);
    }

    public UUID get(String name)
    {
        if (a().containsDoc(name))
        {
            Document document = database.getDocument(name);
            return document.getObject("uniqueId", new TypeToken<UUID>() {
            }.getType());
        }
        return null;
    }

    public String get(UUID uniqueId)
    {
        if (a().containsDoc(uniqueId.toString()))
        {
            Document document = database.getDocument(uniqueId.toString());
            return document.getString("name");
        }
        return null;
    }
}