/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.util;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 16.09.2017.
 */
@Getter
public class QueryDecoder {

    private Map<String, String> queryParams = new ConcurrentHashMap<>();

    public QueryDecoder(String query)
    {
        if (query == null) return;
        if(query.length() == 0 || query.isEmpty() || query.equals("?")) return;

        for (String input : query.split("&"))
        {
            String[] value = input.split("=");
            queryParams.put(value[0], value[1]);
        }
    }
}