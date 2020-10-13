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

package eu.cloudnetservice.cloudnet.v2.api.database;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.database.packet.out.*;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.result.Result;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseImpl implements Database {

    private static final Type MAP_STRING_DOCUMENT_TYPE = TypeToken.getParameterized(Map.class, String.class, Document.class).getType();
    private Map<String, DatabaseDocument> documents = new ConcurrentHashMap<>();
    private final String name;

    DatabaseImpl(String name) {
        this.name = name;
    }

    @Override
    public Map<String, DatabaseDocument> getDocuments() {
        return documents;
    }

    @Override
    public Database loadDocuments() {
        Result result = CloudAPI.getInstance().getNetworkConnection().getPacketManager()
                                .sendQuery(new PacketDBOutGetDocument(name), CloudAPI.getInstance().getNetworkConnection());
        this.documents = result.getResult().getObject("docs", MAP_STRING_DOCUMENT_TYPE);
        return this;
    }

    @Override
    public DatabaseDocument getDocument(String name) {
        Result result = CloudAPI.getInstance().getNetworkConnection().getPacketManager()
                                .sendQuery(new PacketDBOutGetDocument(name, this.name), CloudAPI.getInstance().getNetworkConnection());
        DatabaseDocument document = new DatabaseDocument(result.getResult().getDocument("result"));

        this.documents.put(document.getString(Database.UNIQUE_NAME_KEY), document);

        return document;
    }

    @Override
    public Database insert(DatabaseDocument... documents) {
        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketDBOutInsertDocument(name, documents));
        return this;
    }

    @Override
    public Database delete(String name) {
        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketDBOutDeleteDocument(name, this.name));
        return this;
    }

    @Override
    public Database delete(DatabaseDocument document) {
        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketDBOutDeleteDocument(document, this.name));
        return this;
    }

    @Override
    public DatabaseDocument load(String name) {
        return getDocument(name);
    }

    @Override
    public boolean contains(DatabaseDocument document) {
        return contains(document.getString(Database.UNIQUE_NAME_KEY));
    }

    @Override
    public boolean contains(String name) {
        Result result = CloudAPI.getInstance().getNetworkConnection().getPacketManager()
                                .sendQuery(new PacketDBOutExistsDocument(name, this.name), CloudAPI.getInstance().getNetworkConnection());
        return result.getResult().getBoolean("exists");
    }

    @Override
    public int size() {
        Result result = CloudAPI.getInstance().getNetworkConnection().getPacketManager()
                                .sendQuery(new PacketDBOutGetSize(name), CloudAPI.getInstance().getNetworkConnection());
        return result.getResult().getInt("size");
    }

    @Override
    public Database insertAsync(DatabaseDocument... documents) {
        return insert(documents);
    }

    @Override
    public Database deleteAsync(String name) {
        return delete(name);
    }

    @Override
    public void save() {

    }

    @Override
    public void clear() {
        this.documents.clear();
    }
}
