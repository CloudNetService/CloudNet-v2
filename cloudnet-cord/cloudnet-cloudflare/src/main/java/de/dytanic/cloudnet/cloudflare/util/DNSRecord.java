/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.util;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * General container for storing a DNS record.
 */
@Getter
@AllArgsConstructor
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

}
