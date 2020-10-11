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

import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;

/**
 * This event is called when a player logs out of the network by disconnecting from the proxy.
 * Expect the player to have disconnected when handling the event.
 */
public class ProxiedPlayerLogoutEvent extends ProxiedCloudEvent {

    private final CloudPlayer cloudPlayer;

    public ProxiedPlayerLogoutEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    /**
     * @return the player that left the network.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
