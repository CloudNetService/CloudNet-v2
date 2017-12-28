/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.database;

import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

/**
 * Created by Tareko on 01.07.2017.
 */
@Getter
@AllArgsConstructor
public class DatabaseImpl
            implements Database {

    private final String name;
    private final java.util.Map<String, Document> documents;
    private final File backendDir;
    private final ExecutorService executorService = java.util.concurrent.Executors.newSingleThreadExecutor();

    @Override
    public Database loadDocuments()
    {
        for(File file : backendDir.listFiles())
        {
            if(!this.documents.containsKey(file.getName()))
            this.documents.put(file.getName(), Document.loadDocument(file));
        }
        return this;
    }

    @Override
    public boolean containsDoc(String name)
    {
        return new File("database/" + this.name + "/" + name).exists();
    }

    @Override
    public Collection<Document> getDocs()
    {
        return documents.values();
    }

    @Override
    public Document getDocument(String name)
    {
        Document document = documents.get(name);

        if(document == null)
        {
            File doc = new File("database/" + this.name + "/" + name);
            if(doc.exists())
            {
               document = Document.loadDocument(doc);
               this.documents.put(doc.getName(), document);
               return document;
            }
        }
        return document;
    }

    @Override
    public Database insert(Document... documents)
    {
        for(Document document : documents)
        {
            if(document.contains(UNIQUE_NAME_KEY))
            {
                this.documents.put(document.getString(UNIQUE_NAME_KEY), document);
            }
        }
        return this;
    }

    @Override
    public Database delete(String name)
    {
        Document document = getDocument(name);
        if(document != null)
        {
            documents.remove(document);
        }
        new File("database/" + this.name + "/" + name).delete();
        return this;
    }

    @Override
    public Database delete(Document document)
    {
        this.documents.remove(document);
        new File("database/" + this.name + "/" + name).delete();
        return this;
    }

    @Override
    public Document load(String name)
    {
        return Document.loadDocument(new File("database/databases/" + this.name + "/" + name));
    }

    @Override
    public boolean contains(Document document)
    {
        return contains(document);
    }

    @Override
    public boolean contains(String name)
    {
        return getDocument(name) != null;
    }

    @Override
    public int size()
    {
        return backendDir.list().length;
    }

    @Override
    public Database insertAsync(Document... documents)
    {
        executorService.submit(new Runnable() {
            @Override
            public void run()
            {
                insert(documents);
            }
        });
        return this;
    }

    @Override
    public Database deleteAsync(String name)
    {
        executorService.submit(new Runnable() {
            @Override
            public void run()
            {
                delete(name);
            }
        });
        return this;
    }

    @Override
    public FutureTask<Document> getDocumentAsync(String name)
    {
        FutureTask<Document> get = new FutureTask<>(new Callable<Document>() {
            @Override
            public Document call() throws Exception
            {
                return getDocument(name);
            }
        });
        return get;
    }

    public void save()
    {
        for(Document document : documents.values())
        {
            document.saveAsConfig(new File("database/" + this.name + "/" + document.getString(UNIQUE_NAME_KEY)));
        }
    }

    public void clear()
    {
        this.documents.clear();
    }
}