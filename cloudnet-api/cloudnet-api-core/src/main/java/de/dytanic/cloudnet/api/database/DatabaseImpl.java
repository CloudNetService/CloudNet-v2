/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.database;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.database.packet.out.*;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.network.protocol.packet.result.Result;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 24.08.2017.
 */
public class DatabaseImpl implements Database {

    private static final Type MAP_STRING_DOCUMENT_TYPE = TypeToken.getParameterized(Map.class, String.class, Document.class).getType();
    private Map<String, DatabaseDocument> documents = new ConcurrentHashMap<>();
    private String name;

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
