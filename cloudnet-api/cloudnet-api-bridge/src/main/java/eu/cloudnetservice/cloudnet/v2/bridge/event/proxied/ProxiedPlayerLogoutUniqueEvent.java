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

package eu.cloudnetservice.cloudnet.v2.bridge.event.proxied;

import java.util.UUID;

/**
 * This event is called whenever a player logs out from the cloud network.
 * This event may be called for players which are <b>not</b> registered on CloudNet.
 * When handling this event, the player may already be disconnected.
 */
public class ProxiedPlayerLogoutUniqueEvent extends ProxiedCloudEvent {

    private final UUID uniqueId;

    public ProxiedPlayerLogoutUniqueEvent(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the unique ID of the player that just logged out.
     */
    public UUID getUniqueId() {
        return uniqueId;
    }
}
