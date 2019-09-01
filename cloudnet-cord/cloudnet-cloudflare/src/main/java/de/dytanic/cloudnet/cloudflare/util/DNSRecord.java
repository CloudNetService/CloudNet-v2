/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.util;

import com.google.gson.JsonObject;

/**
 * General container for storing a DNS record.
 */
public class DNSRecord {

    /**
     * The type of this record.
     */
    private String type;

    /**
     * Name of this record like in a zone file
     */
    private String name;

    /**
     * The content of this record like in a zone file
     */
    private String content;

    /**
     * The "Time-to-live" for this record
     */
    private int ttl;

    /**
     * Whether the record should be proxied by CLoudFlare
     */
    private boolean proxied;

    /**
     * Additional data about this record for SRV records
     */
    private JsonObject data;

    public DNSRecord(String type, String name, String content, int ttl, boolean proxied, JsonObject data) {
        this.type = type;
        this.name = name;
        this.content = content;
        this.ttl = ttl;
        this.proxied = proxied;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public int getTtl() {
        return ttl;
    }

    public JsonObject getData() {
        return data;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public boolean isProxied() {
        return proxied;
    }
}
