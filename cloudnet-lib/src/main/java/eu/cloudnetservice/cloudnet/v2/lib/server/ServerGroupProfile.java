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

package eu.cloudnetservice.cloudnet.v2.lib.server;

import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;

public class ServerGroupProfile implements Nameable {

    private final String name;

    private final int maxPlayerCount;

    private final Template config;

    public ServerGroupProfile(String name, int maxPlayerCount, Template config) {
        this.name = name;
        this.maxPlayerCount = maxPlayerCount;
        this.config = config;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public Template getConfig() {
        return config;
    }
}
