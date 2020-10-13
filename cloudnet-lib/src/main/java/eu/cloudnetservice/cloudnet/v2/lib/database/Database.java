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

package eu.cloudnetservice.cloudnet.v2.lib.database;

import java.util.Map;

public interface Database {

    /**
     * Unique key identifying the value that should be used for providing
     * the unique key for a document.
     */
    String UNIQUE_NAME_KEY = "_database_id_unique";

    /**
     * Returns all documents currently loaded in the database.
     * Some implementations may choose to only return documents that
     * are currently loaded in memory, while others might return all
     * documents in the database.
     *
     * @return all documents associated with their unique name.
     */
    Map<String, DatabaseDocument> getDocuments();

    /**
     * Loads all documents of the database into the buffer, if necessary.
     *
     * @return the database.
     */
    Database loadDocuments();

    /**
     * Returns the document with the given name.
     *
     * @param name the name of the document to find.
     *
     * @return the document with the given name or {@code null}, if none could be found.
     */
    DatabaseDocument getDocument(String name);

    /**
     * Inserts the given documents into this database.
     * Some implementations may or may not directly commit
     * the changes to the data store on disk.
     *
     * @param documents the array of documents to insert into the database.
     *
     * @return the database.
     */
    Database insert(DatabaseDocument... documents);

    /**
     * Delete the document with the given name from this database.
     *
     * @param name the name of the document to delete.
     * @return the database.
     */
    Database delete(String name);

    /**
     * Deletes the given database document from the database.
     * This method usually checks the document for an entry with the
     * name {@link #UNIQUE_NAME_KEY} and deletes the document that way.
     *
     * @param document the document to delete.
     *
     * @return the database.
     *
     * @see #delete(String)
     */
    Database delete(DatabaseDocument document);

    /**
     * Loads the document with the given name into memory, if necessary, and returns it.
     *
     * @param name the name of the document to load.
     *
     * @return the loaded database document.
     */
    DatabaseDocument load(String name);

    /**
     * Checks whether this database contains a document with the same name as the given document.
     * This method is similar to {@link Map#containsKey(Object)}.
     *
     * @param document the document to check for.
     *
     * @return whether the database contains a document that matches the name of the given document.
     *
     * @see DatabaseDocument#equals(Object)
     * @see #contains(String)
     */
    boolean contains(DatabaseDocument document);

    /**
     * Checks whether this database contains a document with the given name.
     *
     * @param name the name to check for.
     * @return whether the database contains a document with the given name.
     */
    boolean contains(String name);

    /**
     * Returns the amount of documents in this database.
     * @return the amount of documents in this database.
     */
    int size();

    /**
     * Asynchronously inserts the documents to this database.
     * This method makes no guarantees about the actual asynchronicity of this method.
     *
     * @param documents the documents to insert.
     *
     * @return this database.
     *
     * @see #insert(DatabaseDocument...)
     */
    Database insertAsync(DatabaseDocument... documents);

    /**
     * Asynchronously deletes a document in this database, matching the given name.
     * This method makes no guarantees about the actual asynchronicity of this method.
     *
     * @param name the name of the document to delete.
     * @return this database.
     *
     * @see #delete(String)
     */
    Database deleteAsync(String name);

    /**
     * Saves the currently loaded documents to their files.
     */
    void save();

    /**
     * Clears the currently loaded documents.
     */
    void clear();
}
