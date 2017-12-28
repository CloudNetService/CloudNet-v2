/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.util;

import com.google.gson.JsonObject;

/**
 * Created by Tareko on 26.08.2017.
 */
public class DefaultDNSRecord extends DNSRecord {

    public DefaultDNSRecord(DNSType type, String name, String content, JsonObject data)
    {
        super(type.name(), name, content, 1, false, data);
    }
}