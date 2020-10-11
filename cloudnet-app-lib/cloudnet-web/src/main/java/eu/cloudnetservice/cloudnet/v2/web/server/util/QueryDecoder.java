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

package eu.cloudnetservice.cloudnet.v2.web.server.util;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Utility class for decoding and storing query parameters using a weak hash map.
 */
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
        if (query == null) {
            return;
        }
        if (query.length() == 0 || query.equals("?")) {
            return;
        }

        for (String input : query.split("&")) {
            String[] value = input.split("=");
            queryParams.put(value[0], value[1]);
        }
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
