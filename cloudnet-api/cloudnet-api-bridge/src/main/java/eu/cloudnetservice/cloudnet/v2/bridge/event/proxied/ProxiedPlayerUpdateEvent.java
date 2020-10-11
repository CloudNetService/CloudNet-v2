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
 * This event is called whenever a {@link CloudPlayer} instance is updated on the network.
 * This event does not contain either the prior state nor the nature of or the cause for the update.
 */
public class ProxiedPlayerUpdateEvent extends ProxiedCloudEvent {

    private final CloudPlayer cloudPlayer;

    public ProxiedPlayerUpdateEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    /**
     * @return the newly updated player instance.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
