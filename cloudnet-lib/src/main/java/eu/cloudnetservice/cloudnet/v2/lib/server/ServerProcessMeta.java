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

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.process.ServerProcessData;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.service.plugin.ServerInstallablePlugin;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ServerProcessMeta extends ServerProcessData {

    public static final Type TYPE = TypeToken.get(ServerProcessMeta.class).getType();

    private final int port;

    public ServerProcessMeta(final String wrapperName,
                             final String serverGroupName,
                             final int memory,
                             final ServerConfig serverConfig,
                             final Template template,
                             final List<String> javaProcessParameters,
                             final List<String> serverProcessParameters,
                             final String templateUrl,
                             final Set<ServerInstallablePlugin> plugins,
                             final Properties properties,
                             final ServiceId serviceId,
                             final int port) {
        super(wrapperName,
              serverGroupName,
              memory,
              serverConfig,
              template,
              javaProcessParameters,
              serverProcessParameters,
              templateUrl,
              plugins,
              properties,
              serviceId);
        this.port = port;
    }

    public ServerProcessMeta(final ServerProcessData serverProcessData,
                             final ServiceId serviceId,
                             final int port) {
        super(serverProcessData, serviceId);
        this.port = port;
    }

    public int getPort() {
        return port;
    }

}
