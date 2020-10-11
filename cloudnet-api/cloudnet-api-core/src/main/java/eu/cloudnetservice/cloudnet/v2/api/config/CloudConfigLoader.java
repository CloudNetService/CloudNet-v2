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

package eu.cloudnetservice.cloudnet.v2.api.config;

import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.nio.file.Path;

public class CloudConfigLoader {

    private final Path pathConnectionJson;

    private final Path pathConfigJson;

    private final ConfigTypeLoader type;

    public CloudConfigLoader(Path pathConnectionJson, Path pathConfigJson, ConfigTypeLoader type) {
        this.pathConnectionJson = pathConnectionJson;
        this.pathConfigJson = pathConfigJson;
        this.type = type;
    }

    public ConfigTypeLoader getType() {
        return type;
    }

    public Path getPathConfigJson() {
        return pathConfigJson;
    }

    public Path getPathConnectionJson() {
        return pathConnectionJson;
    }

    public Document loadConfig() {
        return Document.loadDocument(pathConfigJson);
    }

    public ConnectableAddress loadConnection() {
        return Document.loadDocument(pathConnectionJson).getObject("connection", ConnectableAddress.TYPE);
    }

}
