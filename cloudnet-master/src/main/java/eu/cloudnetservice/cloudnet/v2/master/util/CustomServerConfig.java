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

package eu.cloudnetservice.cloudnet.v2.master.util;

public class CustomServerConfig {

    private final String serverId;

    private final int memory;

    private final String group;
    private final String wrapper;

    private final boolean onlineMode;

    public CustomServerConfig(String serverId, int memory, String group, String wrapper, boolean onlineMode) {
        this.serverId = serverId;
        this.memory = memory;
        this.group = group;
        this.wrapper = wrapper;
        this.onlineMode = onlineMode;
    }

    public int getMemory() {
        return memory;
    }

    public String getServerId() {
        return serverId;
    }

    public String getWrapper() {
        return wrapper;
    }

    public String getGroup() {
        return group;
    }
}
