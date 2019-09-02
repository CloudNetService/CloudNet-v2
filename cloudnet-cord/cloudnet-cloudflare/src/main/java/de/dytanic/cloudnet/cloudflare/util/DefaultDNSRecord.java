/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.util;

import com.google.gson.JsonObject;

/**
 * A simple DNS record
 */
public class DefaultDNSRecord extends DNSRecord {

    /**
     * Constructs a simple DNS record, with automatic TTL and no proxying.
     *
     * @param type    the type of this DNS record
     * @param name    the name of this DNS record
     * @param content the content of this DNS record
     * @param data    additional data of this DNS record
     */
    public DefaultDNSRecord(DNSType type, String name, String content, JsonObject data) {
        super(type.name(), name, content, 1, false, data);
    }
}
