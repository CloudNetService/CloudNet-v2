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

package eu.cloudnetservice.cloudnet.v2.lib.user;

import eu.cloudnetservice.cloudnet.v2.lib.hash.DyHash;
import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class User implements Nameable {
    protected String name;
    protected UUID uniqueId;
    protected String apiToken;
    protected String hashedPassword;
    protected Collection<String> permissions;
    protected Map<String, Object> metaData;

    public User(String name,
                UUID uniqueId,
                String apiToken,
                String hashedPassword,
                Collection<String> permissions,
                Map<String, Object> metaData) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.apiToken = apiToken;
        this.hashedPassword = hashedPassword;
        this.permissions = permissions;
        this.metaData = metaData;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (uniqueId != null ? uniqueId.hashCode() : 0);
        result = 31 * result + (apiToken != null ? apiToken.hashCode() : 0);
        result = 31 * result + (hashedPassword != null ? hashedPassword.hashCode() : 0);
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        result = 31 * result + (metaData != null ? metaData.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        final User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(uniqueId, user.uniqueId) && Objects.equals(apiToken,
                                                                                                            user.apiToken) && Objects.equals(
            hashedPassword,
            user.hashedPassword) && Objects.equals(permissions, user.permissions) && Objects.equals(metaData, user.metaData);
    }

    @Override
    public String toString() {
        return "User{" +
            "name='" + name + '\'' +
            ", uniqueId=" + uniqueId +
            ", apiToken='" + apiToken + '\'' +
            ", hashedPassword='" + hashedPassword + '\'' +
            ", permissions=" + permissions +
            ", metaData=" + metaData +
            '}';
    }

    public String getApiToken() {
        return apiToken;
    }

    @Override
    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public SimpledUser toSimple() {
        return new SimpledUser(name, apiToken);
    }

    public boolean hasPermission(String permission) {
        return permissions.contains("*") || permissions.contains(permission);
    }

    public void setPassword(String password) {
        this.hashedPassword = DyHash.hashString(password);
    }
}
