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

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.hash.DyHash;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class BasicUser extends User {

    public BasicUser(String name, String hashedPassword, Collection<String> permissions) {
        super(name, UUID.randomUUID(), NetworkUtils.randomString(16), DyHash.hashString(hashedPassword), permissions, new HashMap<>());
    }
}
