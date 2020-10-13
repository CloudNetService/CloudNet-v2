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
import eu.cloudnetservice.cloudnet.v2.lib.MultiValue;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public final class NameToUUIDDatabase extends DatabaseUsable {

    public NameToUUIDDatabase(Database database) {
        super(database);
    }

    public void append(MultiValue<String, UUID> values) {
        database.insert(new DatabaseDocument(values.getFirst().toLowerCase()).append("uniqueId", values.getSecond()));
        database.insert(new DatabaseDocument(values.getSecond().toString()).append("name", values.getFirst()));
    }

    public void replace(MultiValue<UUID, String> replacer) {
        DatabaseDocument document = database.getDocument(replacer.getFirst().toString());
        document.append("name", replacer.getSecond());
        database.insert(document);
    }

    public UUID get(String name) {
        if (name == null) {
            return null;
        }

        if (database.contains(name.toLowerCase())) {
            DatabaseDocument document = database.getDocument(name.toLowerCase());
            if (!document.contains("uniqueId")) {
                database.delete(name.toLowerCase());
                return null;
            }
            return document.getObject("uniqueId", UUID.class);
        }
        return null;
    }

    public String get(UUID uniqueId) {
        if (uniqueId == null) {
            return null;
        }

        if (database.contains(uniqueId.toString())) {
            DatabaseDocument document = database.getDocument(uniqueId.toString());
            if (!document.contains("name")) {
                database.delete(uniqueId.toString());
                return null;
            }
            return document.getString("name");
        }
        return null;
    }

    public void handleUpdate(UpdateConfigurationDatabase updateConfigurationDatabase) {
        final String updateKey = "updated_database_from_2_1_Pv29";
        if (!updateConfigurationDatabase.get().contains(updateKey)) {
            Collection<DatabaseDocument> documents = new LinkedList<>(database.loadDocuments().getDocuments().values());

            documents.forEach(document -> {
                String name = document.getString(Database.UNIQUE_NAME_KEY);
                if (name != null && name.length() < 32) {
                    database.delete(name);
                    database.insert(document.append(Database.UNIQUE_NAME_KEY, name.toLowerCase()));
                }
            });

            updateConfigurationDatabase.set(updateConfigurationDatabase.get().append(updateKey, true));
            database.save();
            database.clear();
        }
    }
}
