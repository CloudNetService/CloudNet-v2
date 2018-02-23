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

import java.util.Collection;
import java.util.UUID;

/**
 * Created by Tareko on 20.08.2017.
 */
public final class NameToUUIDDatabase extends DatabaseUseable {

    public NameToUUIDDatabase(Database database)
    {
        super(database);
    }

    @Deprecated
    public void handleUpdate(UpdateConfigurationDatabase updateConfigurationDatabase)
    {

        if(!updateConfigurationDatabase.get().contains("updated_database_from_2_1_Pv29"))
        {
            Collection<Document> documents = database.loadDocuments().getDocs();
            String name;

            for (Document document : documents)
            {
                name = document.getString(Database.UNIQUE_NAME_KEY);

                if (name.length() < 32)
                {
                    database.delete(document.getString(Database.UNIQUE_NAME_KEY));
                    database.insert(document.append(Database.UNIQUE_NAME_KEY, name.toLowerCase()));
                }
            }

            updateConfigurationDatabase.set(updateConfigurationDatabase.get().append("updated_database_from_2_1_Pv29", true));
            ((DatabaseImpl)database).save();
            ((DatabaseImpl)database).clear();
        }
    }

    public DatabaseImpl a()
    {
        return ((DatabaseImpl) database);
    }

    public void append(MultiValue<String, UUID> values)
    {
        if (!a().containsDoc(values.getFirst().toLowerCase()))
            database.insert(new DatabaseDocument(values.getFirst().toLowerCase()).append("uniqueId", values.getSecond()));
        else
            database.insert(database.getDocument(values.getFirst().toLowerCase()).append("uniqueId", values.getSecond()));

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
        if (name == null) return null;

        if (a().containsDoc(name.toLowerCase()))
        {
            Document document = database.getDocument(name.toLowerCase());
            return document.getObject("uniqueId", new TypeToken<UUID>() {}.getType());
        }
        return null;
    }

    public String get(UUID uniqueId)
    {
        if (uniqueId == null) return null;

        if (a().containsDoc(uniqueId.toString()))
        {
            Document document = database.getDocument(uniqueId.toString());
            return document.getString("name");
        }
        return null;
    }
}