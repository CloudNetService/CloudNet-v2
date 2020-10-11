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
 * This event is called whenever a player has switched their current server.
 */
public class ProxiedPlayerServerSwitchEvent extends ProxiedCloudEvent {

    private final CloudPlayer cloudPlayer;

    private final String server;

    public ProxiedPlayerServerSwitchEvent(CloudPlayer cloudPlayer, String server) {
        this.cloudPlayer = cloudPlayer;
        this.server = server;
    }

    /**
     * @return the player that just switched to another server.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    /**
     * @return the server-id the player switched to.
     */
    public String getServer() {
        return server;
    }
}
