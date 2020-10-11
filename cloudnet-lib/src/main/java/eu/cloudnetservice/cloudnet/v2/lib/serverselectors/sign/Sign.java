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

package eu.cloudnetservice.cloudnet.v2.lib.serverselectors.sign;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

public class Sign {

    public static final Type TYPE = TypeToken.get(Sign.class).getType();
    private final UUID uniqueId;
    private final String targetGroup;
    private final Position position;

    private transient ServerInfo serverInfo;

    public Sign(String targetGroup, Position signPosition) {
        this.uniqueId = UUID.randomUUID();
        this.targetGroup = targetGroup;
        this.position = signPosition;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public int hashCode() {
        int result = uniqueId != null ? uniqueId.hashCode() : 0;
        result = 31 * result + (targetGroup != null ? targetGroup.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (serverInfo != null ? serverInfo.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sign)) {
            return false;
        }

        final Sign sign = (Sign) o;

        if (!Objects.equals(uniqueId, sign.uniqueId)) {
            return false;
        }
        if (!Objects.equals(targetGroup, sign.targetGroup)) {
            return false;
        }
        if (!Objects.equals(position, sign.position)) {
            return false;
        }
        return Objects.equals(serverInfo, sign.serverInfo);
    }

    @Override
    public String toString() {
        return "de.dytanic.cloudnet.lib.serverselectors.sign.Sign{" +
            "uniqueId=" + uniqueId +
            ", targetGroup='" + targetGroup + '\'' +
            ", position=" + position +
            ", serverInfo=" + serverInfo +
            '}';
    }

    public Position getPosition() {
        return position;
    }

    public String getTargetGroup() {
        return targetGroup;
    }
}
