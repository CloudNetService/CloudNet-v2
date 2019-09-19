/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.database;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * Implementation of {@link Database}.
 */
public class DatabaseImpl implements Database {

    private final String name;
    private final java.util.Map<String, Document> documents;
    private final File backendDir;

    public DatabaseImpl(String name, Map<String, Document> documents, File backendDir) {
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

    public Map<String, Document> getDocuments() {
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
                Document document = Document.loadDocument(file);
                if (document.contains(UNIQUE_NAME_KEY)) {
                    this.documents.put(file.getName(), document);
                }
            }
        }
        return this;
    }

    @Override
    public Collection<Document> getDocs() {
        return documents.values();
    }

    @Override
    public Document getDocument(String name) {
        if (name == null) {
            return null;
        }

        Document document = documents.get(name);

        if (document == null) {
            File doc = new File("database/" + this.name + NetworkUtils.SLASH_STRING + name);
            if (doc.exists()) {
                document = Document.loadDocument(doc);
                this.documents.put(doc.getName(), document);
                return document;
            }
        }
        return document;
    }

    @Override
    public Database insert(Document... documents) {
        for (Document document : documents) {
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

        Document document = getDocument(name);
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
    public Database delete(Document document) {
        if (document.contains(UNIQUE_NAME_KEY)) {
            delete(document.getString(UNIQUE_NAME_KEY));
        }
        return this;
    }

    @Override
    public Document load(String name) {
        return Document.loadDocument(new File("database/" + this.name + NetworkUtils.SLASH_STRING + name));
    }

    @Override
    public boolean contains(Document document) {
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
    public boolean containsDoc(String name) {
        if (name == null) {
            return false;
        }
        return new File("database/" + this.name + NetworkUtils.SLASH_STRING + name).exists();
    }

    @Override
    public Database insertAsync(Document... documents) {
        TaskScheduler.runtimeScheduler().schedule(() -> {
            insert(documents);
        });
        return this;
    }

    @Override
    public Database deleteAsync(String name) {
        TaskScheduler.runtimeScheduler().schedule(() -> {
            delete(name);
        });
        return this;
    }

    @Override
    public FutureTask<Document> getDocumentAsync(String name) {
        return new FutureTask<>(() -> getDocument(name));
    }

    /**
     * Saves the currently loaded documents to their files.
     */
    public void save() {
        for (Document document : documents.values()) {
            if (document.contains(UNIQUE_NAME_KEY)) {
                document.saveAsConfig(Paths.get("database", this.name, document.getString(UNIQUE_NAME_KEY)));
            }
        }
    }

    /**
     * Clears the currently loaded documents.
     */
    public void clear() {
        this.documents.clear();
    }
}
