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

import java.net.InetAddress;
import java.util.Objects;

public class WrapperMeta {

    private final String id;

    private final InetAddress hostName;

    private final String user;

    public WrapperMeta(String id, InetAddress hostName, String user) {
        this.id = id;
        this.hostName = hostName;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public InetAddress getHostName() {
        return hostName;
    }

    public String getUser() {
        return user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hostName, user);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WrapperMeta)) {
            return false;
        }
        final WrapperMeta that = (WrapperMeta) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(hostName, that.hostName) &&
            Objects.equals(user, that.user);
    }
}
