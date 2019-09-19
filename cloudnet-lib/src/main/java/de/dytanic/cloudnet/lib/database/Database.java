package de.dytanic.cloudnet.lib.database;

import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Collection;
import java.util.concurrent.FutureTask;

/**
 * Created by Tareko on 01.07.2017.
 */
public interface Database {

    String UNIQUE_NAME_KEY = "_database_id_unique";

    static DatabaseDocument createEmptyDocument(String name) {
        return new DatabaseDocument(name);
    }

    Database loadDocuments();

    Collection<Document> getDocs();

    Document getDocument(String name);

    Database insert(Document... documents);

    Database delete(String name);

    Database delete(Document document);

    Document load(String name);

    boolean contains(Document document);

    boolean contains(String name);

    int size();

    boolean containsDoc(String name);

    Database insertAsync(Document... documents);

    Database deleteAsync(String name);

    FutureTask<Document> getDocumentAsync(String name);
}
