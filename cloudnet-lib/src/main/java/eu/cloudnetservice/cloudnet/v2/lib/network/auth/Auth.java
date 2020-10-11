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

package eu.cloudnetservice.cloudnet.v2.lib.network.auth;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.user.User;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.lang.reflect.Type;

public final class Auth {

    public static final Type TYPE = TypeToken.get(Auth.class).getType();
    private final AuthType type;
    private Document authData = new Document();

    public Auth(AuthType type, Document authData) {
        this.type = type;
        this.authData = authData;
    }

    public Auth(String serviceKey, String cloudNetId) {
        this.type = AuthType.CLOUD_NET;
        this.authData.append("key", serviceKey).append("id", cloudNetId);
    }

    public Auth(ServiceId serverId) {
        this.type = AuthType.GAMESERVER_OR_BUNGEE;
        this.authData.append("serviceId", serverId);
    }

    public Auth(User user) {
        this.type = AuthType.GAMESERVER_OR_BUNGEE;
        this.authData.append("user", user);
    }

    public AuthType getType() {
        return type;
    }

    public Document getAuthData() {
        return authData;
    }
}
