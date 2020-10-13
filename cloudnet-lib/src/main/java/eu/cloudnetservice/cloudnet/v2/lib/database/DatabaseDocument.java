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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Document that is used in conjunction with a {@link Database}.
 * This class provides type-safe and type-ignorant access to stored objects.
 * <p>
 * Care must be taken to prevent cyclic references in appended objects, as those can not
 * be resolved and lead to exceptions or undefined behaviour.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DatabaseDocument implements org.dizitart.no2.mapper.Mappable {

    /**
     * Field to store the current unique key for this document.
     */
    @Id
    private String _database_id_unique;

    /**
     * The actual data store for this object.
     */
    private Document backingDocument;

    /**
     * Constructs a new database document with the given name.
     * The name is also used as the unique key.
     *
     * @param name the unique name of this document.
     */
    public DatabaseDocument(String name) {
        this();
        this.append(Database.UNIQUE_NAME_KEY, name);
    }

    /**
     * Constructs a new database document without a name.
     * Saving this document to a database prior to setting a unique name may fail.
     */
    public DatabaseDocument() {
        super();
        this.backingDocument = new Document();
    }

    /**
     * Associates the given object with the given name, replacing the old value.
     *
     * @param name   the name to associate the object with.
     * @param object the object to associate the key with.
     *
     * @return this document.
     */
    public DatabaseDocument append(final String name, final Object object) {
        if (Database.UNIQUE_NAME_KEY.equals(name) && object instanceof String) {
            this._database_id_unique = (String) object;
        }

        this.backingDocument.append(name, object);
        return this;
    }

    /**
     * Constructs a new database document from an ordinary document.
     * The document is directly used as the delegate for mutating methods.
     *
     * @param document the document to use as the backing delegate.
     */
    public DatabaseDocument(final Document document) {
        this();
        this.backingDocument = document;
        if (document.contains(Database.UNIQUE_NAME_KEY)) {
            this._database_id_unique = document.getString(Database.UNIQUE_NAME_KEY);
        }
    }

    /**
     * Constructs a new database document from a map of String-Object associations.
     *
     * @param map the key-value map to insert in to this document.
     */
    public DatabaseDocument(final Map<String, Object> map) {
        this();
        this.putAll(map);
    }

    /**
     * Adds all key-value associations in the given map to this document.
     * If a key is already associated with a value in this document, the value will be replaced.
     *
     * @param map the map to add all key-value associations from.
     */
    public void putAll(final Map<? extends String, ?> map) {
        map.forEach(this::append);
    }

    /**
     * <p>Recursively parses a JSON element into a structured data type, depending on its contents.</p>
     * <p>
     * If the JSON element is null, this method returns {@code null}. <br>
     * If the JSON element is a primitive, this method returns the primitive in its object type,
     * due to dynamic typing.<br>
     * If the JSON element is an array, this method returns a list of further parsed JSON elements.<br>
     * If the JSON element is an object, this method returns a map of further parsed JSON element.<br>
     *
     * @param value the JSON element to parse into a structured data type.
     *
     * @return the parsed JSON element.
     *
     * @see #parseJsonArray(JsonArray)
     * @see #parseJsonPrimitive(JsonPrimitive)
     * @see #parseJsonObject(JsonObject)
     */
    private static Object parseJsonElement(final JsonElement value) {
        if (value.isJsonNull()) {
            return null;
        }
        if (value.isJsonObject()) {
            return parseJsonObject(value.getAsJsonObject());
        } else if (value.isJsonArray()) {
            return parseJsonArray(value.getAsJsonArray());
        } else if (value.isJsonPrimitive()) {
            return parseJsonPrimitive(value.getAsJsonPrimitive());
        }
        return null;
    }

    /**
     * Recursively parses a JSON object into a map of string key and value associations.
     *
     * @param object the JSON object to parse.
     *
     * @return the parsed JSON object with its key-value associations in a map.
     *
     * @see #parseJsonElement(JsonElement)
     */
    private static Map<String, Object> parseJsonObject(final JsonObject object) {
        Map<String, Object> map = new HashMap<>();
        object.entrySet().forEach(entry -> map.put(entry.getKey(), parseJsonElement(entry.getValue())));
        return map;
    }

    /**
     * Parses a JSON array into a list of parsed JSON elements.
     *
     * @param array the JSON array to parse.
     *
     * @return the parsed JSON array with its parsed elements in the order they were in the source array.
     *
     * @see #parseJsonElement(JsonElement)
     */
    private static List<Object> parseJsonArray(final JsonArray array) {
        if (array.size() == 0) {
            return Collections.emptyList();
        } else {
            List<Object> list = new ArrayList<>();
            array.forEach(element -> list.add(parseJsonElement(element)));
            return list;
        }
    }

    /**
     * Parses a primitive JSON value.
     *
     * @param value the value to parse.
     *
     * @return the parsed value in its container/wrapped type (ie. {@link Integer}.
     *
     * @see #parseJsonElement(JsonElement)
     */
    private static Object parseJsonPrimitive(final JsonPrimitive value) {
        if (value.isNumber()) {
            return value.getAsNumber();
        } else if (value.isBoolean()) {
            return value.getAsBoolean();
        } else if (value.isString()) {
            return value.getAsString();
        } else {
            return value.getAsCharacter();
        }
    }

    @Override
    public Object clone() {
        DatabaseDocument clone;
        try {
            clone = (DatabaseDocument) super.clone();
        } catch (CloneNotSupportedException e) {
            clone = new DatabaseDocument();
        }
        final DatabaseDocument finalClone = clone;
        this.forEach(finalClone::append);
        return clone;
    }

    @Override
    public String toString() {
        return "DatabaseDocument{" +
            "_database_id_unique='" + _database_id_unique + '\'' +
            ", backingDocument=" + backingDocument +
            '}';
    }

    /**
     * Performs the given {@code action} on each key-value pair of this document.
     * The values are read as generic {@link Object} instances, so no guarantees about the type
     * can be made.
     *
     * @param action the action to be performed on each element of this document.
     */
    public void forEach(final BiConsumer<? super String, ? super Object> action) {
        this.backingDocument
            .keys()
            .forEach(key -> action.accept(key, this.get(key)));
    }

    /**
     * Gets the generic object instance for the given key.
     * This method is the same as calling {@link #getObject(String, Type)} with {@code key, Object.class}
     * as parameters, so no guarantees about the type can be made.
     *
     * @param key the key to get the associated value for.
     *
     * @return the object associated to the given key.
     *
     * @see #getObject(String, Type)
     */
    public Object get(final String key) {
        return this.getObject(key, Object.class);
    }

    /**
     * Gets the object associated to the given key deserialized to the supplied type.
     * This method allows conversion to happen between types which do not share hierarchy.
     *
     * @param key  the key to get the associated value for.
     * @param type the type that the value should be deserialized into.
     * @param <T>  the generic type of the returned object (usually inferred).
     *
     * @return the object in the given type associated to the given key.
     */
    public <T> T getObject(final String key, Type type) {
        Objects.requireNonNull(key, "key must not be null!");
        Objects.requireNonNull(type, "type must not be null!");
        return this.backingDocument.getObject(key, type);
    }

    /**
     * Saves this document as a JSON file to the specified path.
     *
     * @param path the path to save this document to.
     *
     * @return whether the process of saving the file was successful.
     */
    public boolean saveAsConfig(final Path path) {
        return this.backingDocument.saveAsConfig(path);
    }

    /**
     * Returns the number of entries in this document.
     *
     * @return the number of entries in this document.
     */
    public int size() {
        return this.backingDocument.size();
    }

    /**
     * Returns whether this document is empty.
     *
     * @return whether this document is empty.
     */
    public boolean isEmpty() {
        return this.backingDocument.isEmpty();
    }

    /**
     * Returns whether this document contains a value associated to the given key.
     *
     * @param key the ke to check for.
     *
     * @return whether there exists a value for the given key.
     */
    public boolean contains(final String key) {
        return this.backingDocument.contains(key);
    }

    /**
     * Removes the key-value association for the given key.
     *
     * @param key the key to remove the association for.
     *
     * @return this document.
     */
    public DatabaseDocument remove(final String key) {
        this.backingDocument.remove(key);
        return this;
    }

    /**
     * Convenience method to retrieve a string out of this document.
     *
     * @param key the key to retrieve the string for.
     *
     * @return the string associated to the given key.
     */
    public String getString(final String key) {
        return this.backingDocument.getString(key);
    }

    /**
     * Returns the value associated with the given key of the given type, if present.
     * If not, returns given the default value.
     *
     * @param key          the key to get the associated value for.
     * @param defaultValue the default value, if there is no value associated to the given key.
     * @param type         the type of the value to get.
     * @param <T>          the type to return. Both the value and the default value must have this type.
     *
     * @return the value associated with the given key or defaultValue
     */
    public <T> T getOrDefault(final String key, final T defaultValue, final Type type) {
        Objects.requireNonNull(defaultValue, "defaultValue can not be null!");
        T value = this.getObject(key, type);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Clears all key-value associations in this document.
     */
    public void clear() {
        this.backingDocument.clear();
    }

    /**
     * Returns a set of all keys in this document that are associated to a value.
     *
     * @return a set of all keys.
     */
    public Set<String> keySet() {
        return this.backingDocument.keys();
    }

    /**
     * Collects and returns a list of all values in this document.
     *
     * @return a list of all values in this document.
     */
    public Collection<Object> values() {
        return this.backingDocument
            .keys().stream()
            .map(this::get)
            .collect(Collectors.toList());
    }

    /**
     * Collects all entries as key-value pairs of this document and returns it.
     * Due to the nature of dynamic typing, all objects are collected using the {@link Object} type.
     * This means that no guarantees about the types of the values can be made.
     *
     * @return a set containing all key-value pairs of this document.
     */
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.backingDocument
            .keys().stream()
            .collect(Collectors.toMap(Function.identity(), this::get))
            .entrySet();
    }

    /**
     * Convenience method to retrieve a long out of this document.
     *
     * @param key the key to retrieve the long for.
     *
     * @return the long associated to the given key.
     */
    public long getLong(final String key) {
        return this.backingDocument.getLong(key);
    }

    /**
     * Convenience method to retrieve a integer out of this document.
     *
     * @param key the key to retrieve the integer for.
     *
     * @return the integer associated to the given key.
     */
    public int getInt(final String key) {
        return this.backingDocument.getInt(key);
    }

    /**
     * Returns the backing document delegate of this document.
     *
     * @return the backing document of this document.
     */
    public Document toDocument() {
        return this.backingDocument;
    }

    /**
     * Convenience method to retrieve a new document out of this document.
     *
     * @param key the key to retrieve the document for.
     *
     * @return the document associated to the given key.
     */
    public Document getDocument(final String key) {
        return this.backingDocument.getDocument(key);
    }

    @Override
    public org.dizitart.no2.Document write(final NitriteMapper mapper) {
        return mapper.asDocument(parseJsonElement(this.backingDocument.obj()));
    }

    @Override
    public void read(final NitriteMapper mapper, final org.dizitart.no2.Document document) {
        this.backingDocument = new Document();
        final org.dizitart.no2.Document parsedDocument = mapper.asDocument(document);
        if (parsedDocument != null) {
            parsedDocument.forEach(this::append);
        }
    }

}
