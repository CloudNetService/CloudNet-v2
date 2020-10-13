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
import eu.cloudnetservice.cloudnet.v2.lib.process.ProxyProcessData;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.service.plugin.ServerInstallablePlugin;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * Class holding data about an actual process that can be used to launch a proxy.
 */
public class ProxyProcessMeta extends ProxyProcessData {

    public static final Type TYPE = TypeToken.get(ProxyProcessMeta.class).getType();
    private final int port;

    public ProxyProcessMeta(final String wrapperName,
                            final String proxyGroupName,
                            final int memory,
                            final List<String> javaProcessParameters,
                            final List<String> proxyProcessParameters,
                            final String templateUrl,
                            final Set<ServerInstallablePlugin> plugins,
                            final Document properties,
                            final ServiceId serviceId,
                            final int port) {
        super(wrapperName,
              proxyGroupName,
              memory,
              javaProcessParameters,
              proxyProcessParameters,
              templateUrl,
              plugins,
              properties,
              serviceId);
        this.port = port;
    }

    public ProxyProcessMeta(final ProxyProcessData proxyProcessData,
                            final ServiceId serviceId,
                            final int port) {
        super(proxyProcessData, serviceId);
        this.port = port;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final ProxyProcessMeta that = (ProxyProcessMeta) o;

        return port == that.port;
    }

    @Override
    public String toString() {
        return "ProxyProcessMeta{" +
            "port=" + port +
            "} " + super.toString();
    }

    public int getPort() {
        return port;
    }

}
