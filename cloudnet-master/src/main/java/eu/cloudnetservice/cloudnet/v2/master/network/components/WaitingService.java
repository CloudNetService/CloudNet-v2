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

package eu.cloudnetservice.cloudnet.v2.master.network.components;

import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;

import java.util.Objects;

public class WaitingService {
    private final int port;
    private final int usedMemory;
    private final ServiceId serviceId;
    private final Template template;

    public WaitingService(final int port,
                          final int usedMemory,
                          final ServiceId serviceId,
                          final Template template) {
        this.port = port;
        this.usedMemory = usedMemory;
        this.serviceId = serviceId;
        this.template = template;
    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + usedMemory;
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        result = 31 * result + (template != null ? template.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WaitingService)) {
            return false;
        }

        final WaitingService that = (WaitingService) o;

        if (port != that.port) {
            return false;
        }
        if (usedMemory != that.usedMemory) {
            return false;
        }
        if (!Objects.equals(serviceId, that.serviceId)) {
            return false;
        }
        return Objects.equals(template, that.template);
    }

    @Override
    public String toString() {
        return "WaitingService{" +
            "port=" + port +
            ", usedMemory=" + usedMemory +
            ", serviceId=" + serviceId +
            ", template=" + template +
            '}';
    }

    public int getPort() {
        return port;
    }

    public int getUsedMemory() {
        return usedMemory;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public Template getTemplate() {
        return template;
    }
}
