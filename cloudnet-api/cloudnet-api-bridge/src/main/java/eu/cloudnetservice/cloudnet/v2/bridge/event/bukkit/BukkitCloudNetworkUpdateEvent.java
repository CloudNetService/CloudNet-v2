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

package eu.cloudnetservice.cloudnet.v2.bridge.event.bukkit;

import eu.cloudnetservice.cloudnet.v2.api.handlers.NetworkHandler;
import eu.cloudnetservice.cloudnet.v2.lib.CloudNetwork;
import org.bukkit.event.HandlerList;

/**
 * This event is called when the CloudNetwork is updated.
 * <p>
 * {@link NetworkHandler#onCloudNetworkUpdate(CloudNetwork)}
 * </p>
 */
public class BukkitCloudNetworkUpdateEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final CloudNetwork cloudNetwork;

    /**
     * Constructs a new event for notifying other plugins that the CloudNetwork has been updated.
     *
     * @param cloudNetwork the new state of the cloud network.
     */
    public BukkitCloudNetworkUpdateEvent(CloudNetwork cloudNetwork) {
        super();
        this.cloudNetwork = cloudNetwork;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the new updated state of the cloud network.
     */
    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
