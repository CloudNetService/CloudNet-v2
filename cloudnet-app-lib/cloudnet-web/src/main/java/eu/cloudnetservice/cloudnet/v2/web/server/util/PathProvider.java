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

import eu.cloudnetservice.cloudnet.v2.lib.map.WrappedMap;

/**
 * Data class that holds information for a dynamic path like in Spring
 */
public class PathProvider {

    /**
     * The path where a request has been sent to.
     */
    private final String path;

    /**
     * The parameters of a request to {@code path}.
     */
    private final WrappedMap pathParameters;

    public PathProvider(String path, WrappedMap pathParameters) {
        this.path = path;
        this.pathParameters = pathParameters;
    }

    public String getPath() {
        return path;
    }

    public WrappedMap getPathParameters() {
        return pathParameters;
    }
}
