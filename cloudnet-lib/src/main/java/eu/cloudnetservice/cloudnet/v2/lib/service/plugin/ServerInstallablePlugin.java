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

package eu.cloudnetservice.cloudnet.v2.lib.service.plugin;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;

public class ServerInstallablePlugin {

    public static final Type TYPE = TypeToken.get(ServerInstallablePlugin.class).getType();
    public static final Type SET_TYPE = TypeToken.getParameterized(Set.class, ServerInstallablePlugin.class).getType();

    private final String name;
    private final PluginResourceType pluginResourceType;
    private final String url;

    public ServerInstallablePlugin(String name, PluginResourceType pluginResourceType, String url) {
        this.name = name;
        this.pluginResourceType = pluginResourceType;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public PluginResourceType getPluginResourceType() {
        return pluginResourceType;
    }
}
