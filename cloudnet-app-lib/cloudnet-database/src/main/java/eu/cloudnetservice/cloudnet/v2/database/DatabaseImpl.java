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

package eu.cloudnetservice.cloudnet.v2.database;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Implementation of {@link Database}.
 */
public class DatabaseImpl implements Database {

    /**
     * The name of this database.
     * Used for determining the directory to use.
     */
    private final String name;

    /**
     * The currently loaded documents.
     * In this implementation, many database accesses try to access this map first.
     */
    private final Map<String, DatabaseDocument> documents;

    /**
     * Directory where all the files of this database are stored.
     */
    private final File backendDir;

    public DatabaseImpl(String name, Map<String, DatabaseDocument> documents, File backendDir) {
        this.name = name;
        this.documents = documents;
        this.backendDir = backendDir;
    }

    public String getName() {
        return name;
    }

    public File getBackendDir() {
        return backendDir;
    }

    @Override
    public Map<String, DatabaseDocument> getDocuments() {
        return documents;
    }

    @Override
    public Database loadDocuments() {
        File[] files = backendDir.listFiles();
        if (files == null) {
            return this;
        }
        for (File file : files) {
            if (!this.documents.containsKey(file.getName())) {
                DatabaseDocument document = new DatabaseDocument(Document.loadDocument(file));
                if (document.contains(UNIQUE_NAME_KEY)) {
                    this.documents.put(file.getName(), document);
                }
            }
        }
        return this;
    }

    @Override
    public DatabaseDocument getDocument(String name) {
        if (name == null) {
            return null;
        }

        DatabaseDocument document = documents.get(name);

        if (document == null) {
            Path doc = Paths.get(this.backendDir.getAbsolutePath(), name);
            if (Files.exists(doc)) {
                document = new DatabaseDocument(Document.loadDocument(doc));
                this.documents.put(document.getString(Database.UNIQUE_NAME_KEY), document);
                return document;
            }
        }
        return document;
    }

    @Override
    public Database insert(DatabaseDocument... documents) {
        for (DatabaseDocument document : documents) {
            if (document.contains(UNIQUE_NAME_KEY)) {
                this.documents.put(document.getString(UNIQUE_NAME_KEY), document);
                Path path = Paths.get("database/" + this.name + '/' + document.getString(UNIQUE_NAME_KEY));
                if (!Files.exists(path)) {
                    try {
                        Files.createFile(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                document.saveAsConfig(path);
            }
        }
        return this;
    }

    @Override
    public Database delete(String name) {
        if (name == null) {
            return this;
        }

        DatabaseDocument document = getDocument(name);
        if (document != null) {
            documents.remove(name);
        }
        try {
            Files.delete(Paths.get("database", this.name, name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Database delete(DatabaseDocument document) {
        if (document.contains(UNIQUE_NAME_KEY)) {
            delete(document.getString(UNIQUE_NAME_KEY));
        }
        return this;
    }

    @Override
    public DatabaseDocument load(String name) {
        return new DatabaseDocument(Document.loadDocument(Paths.get("database", this.name, name)));
    }

    @Override
    public boolean contains(DatabaseDocument document) {
        return contains(document.getString(UNIQUE_NAME_KEY));
    }

    @Override
    public boolean contains(String name) {
        return getDocument(name) != null;
    }

    @Override
    public int size() {
        String[] files = backendDir.list();
        return files == null ? 0 : files.length;
    }

    @Override
    public Database insertAsync(DatabaseDocument... documents) {
        NetworkUtils.getExecutor().submit(() -> insert(documents));
        return this;
    }

    @Override
    public Database deleteAsync(String name) {
        NetworkUtils.getExecutor().submit(() -> delete(name));
        return this;
    }

    @Override
    public void save() {
        for (DatabaseDocument document : documents.values()) {
            if (document.contains(UNIQUE_NAME_KEY)) {
                document.saveAsConfig(Paths.get("database", this.name, document.getString(UNIQUE_NAME_KEY)));
            }
        }
    }

    @Override
    public void clear() {
        this.documents.clear();
    }
}
