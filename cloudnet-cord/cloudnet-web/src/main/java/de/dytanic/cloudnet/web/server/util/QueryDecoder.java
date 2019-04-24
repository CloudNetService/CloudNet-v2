/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.util;

import lombok.Getter;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Utility class for decoding and storing query parameters using a weak hash map.
 */
@Getter
public class QueryDecoder {

    /**
     * The decoded query parameters.
     */
    private final Map<String, String> queryParams = new WeakHashMap<>();

    /**
     * Constructs a new query decoder for a given query.
     * Decodes the query immediately.
     *
     * @param query the query to decode.
     */
    public QueryDecoder(String query) {
        if (query == null) return;
        if (query.length() == 0 || query.equals("?")) return;

        for (String input : query.split("&")) {
            String[] value = input.split("=");
            queryParams.put(value[0], value[1]);
        }
    }
}
