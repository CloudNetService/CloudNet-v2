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

package eu.cloudnetservice.cloudnet.v2.database.nitrite;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import org.dizitart.no2.IndexOptions;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link Database} interface using the Nitrite database.
 * This class uses the {@link ObjectRepository} to allow access to {@link DatabaseDocument} instances.
 */
public class NitriteDatabase implements Database {

    /**
     * THe object repository that is used to store and retrieve database documents from.
     */
    private final ObjectRepository<DatabaseDocument> repository;

    /**
     * The containing database store of this database.
     * Used for committing changes.
     *
     * @see Nitrite#commit()
     */
    private final Nitrite database;

    /**
     * Constructs a new database with the given name and Nitrite database.
     * Adds an index to the key {@link #UNIQUE_NAME_KEY}, if one is not already present.
     *
     * @param name     the name of this database.
     * @param database the Nitrite database where this database is stored in.
     */
    public NitriteDatabase(final String name, final Nitrite database) {
        this.database = database;
        this.repository = database.getRepository(name, DatabaseDocument.class);
        if (!this.repository.hasIndex(UNIQUE_NAME_KEY)) {
            this.repository.createIndex(UNIQUE_NAME_KEY, IndexOptions.indexOptions(IndexType.Unique));
        }
    }

    @Override
    public Map<String, DatabaseDocument> getDocuments() {
        Cursor<DatabaseDocument> documentCursor = repository.find();
        Map<String, DatabaseDocument> map = new HashMap<>();
        for (final DatabaseDocument document : documentCursor) {
            map.put(document.getString(UNIQUE_NAME_KEY), document);
        }
        return map;
    }

    @Override
    public Database loadDocuments() {
        // No implementation as it is not needed
        return this;
    }

    @Override
    public DatabaseDocument getDocument(final String name) {
        return repository.find(ObjectFilters.eq(UNIQUE_NAME_KEY, name)).firstOrDefault();
    }

    @Override
    public Database insert(final DatabaseDocument... documents) {
        for (final DatabaseDocument document : documents) {
            if (document.contains(Database.UNIQUE_NAME_KEY)) {
                repository.update(document, true);
            } else {
                throw new IllegalArgumentException("Missing unique key in document.");
            }
        }
        this.save();
        return this;
    }

    @Override
    public Database delete(final String name) {
        repository.remove(ObjectFilters.eq(UNIQUE_NAME_KEY, name));
        return this;
    }

    @Override
    public Database delete(final DatabaseDocument document) {
        if (document.contains(UNIQUE_NAME_KEY)) {
            delete(document.getString(UNIQUE_NAME_KEY));
        } else {
            throw new IllegalArgumentException("document does not contain a unique key to delete by!");
        }
        return this;
    }

    @Override
    public DatabaseDocument load(final String name) {
        return getDocument(name);
    }

    @Override
    public boolean contains(final DatabaseDocument document) {
        return document.contains(UNIQUE_NAME_KEY) && this.contains(document.getString(UNIQUE_NAME_KEY));
    }

    @Override
    public boolean contains(final String name) {
        return repository.find(ObjectFilters.eq(UNIQUE_NAME_KEY, name)).firstOrDefault() != null;
    }

    @Override
    public int size() {
        return (int) repository.size();
    }

    @Override
    public Database insertAsync(final DatabaseDocument... documents) {
        NetworkUtils.getExecutor().submit(() -> insert(documents));
        return this;
    }

    @Override
    public Database deleteAsync(final String name) {
        NetworkUtils.getExecutor().submit(() -> delete(name));
        return this;
    }

    @Override
    public void save() {
        this.database.commit();
    }

    /**
     * In this implementation, the clear method does not do anything, as there is
     * nothing to clear.
     */
    @Override
    public void clear() {
        // Not implemented as it's not needed.
    }

}
