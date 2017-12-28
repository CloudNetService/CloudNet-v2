/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.util;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 26.08.2017.
 */
@Getter
@AllArgsConstructor
public class DNSRecord {

    private String type;

    private String name;

    private String content;

    private int ttl;

    private boolean proxied;

    private JsonObject data;

}