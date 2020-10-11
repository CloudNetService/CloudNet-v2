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

package eu.cloudnetservice.cloudnet.v2.lib.service;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

public final class ServiceId {

    public static final Type TYPE = TypeToken.get(ServiceId.class).getType();

    private final String group;
    private final int id;
    private final UUID uniqueId;
    private final String wrapperId;
    private final String serverId;

    public ServiceId(String groupName, int id, String wrapperName) {
        this.group = groupName;
        this.id = id;
        this.uniqueId = UUID.randomUUID();
        this.wrapperId = wrapperName;

        this.serverId = group + '-' + id;
    }

    public ServiceId(String group, int id, UUID uniqueId, String wrapperId, String serverId) {
        this.group = group;
        this.id = id;
        this.uniqueId = uniqueId;
        this.wrapperId = wrapperId;

        this.serverId = serverId;
    }

    public String getServerId() {
        return serverId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getGroup() {
        return group;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int result = group != null ? group.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (uniqueId != null ? uniqueId.hashCode() : 0);
        result = 31 * result + (wrapperId != null ? wrapperId.hashCode() : 0);
        result = 31 * result + (serverId != null ? serverId.hashCode() : 0);
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

        final ServiceId serviceId = (ServiceId) o;

        if (id != serviceId.id) {
            return false;
        }
        if (!Objects.equals(group, serviceId.group)) {
            return false;
        }
        if (!Objects.equals(uniqueId, serviceId.uniqueId)) {
            return false;
        }
        if (!Objects.equals(wrapperId, serviceId.wrapperId)) {
            return false;
        }
        return Objects.equals(serverId, serviceId.serverId);
    }

    @Override
    public String toString() {
        return this.serverId + '#' + this.uniqueId;
    }
}
