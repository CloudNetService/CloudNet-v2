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

import eu.cloudnetservice.cloudnet.v2.bridge.CloudServer;
import org.bukkit.event.HandlerList;

/**
 * This event is called right before the first (initial) update of the current cloud server is being done.
 * This event can be used to determine, when the server is ready to accept connections.
 */
public class BukkitCloudServerInitEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final CloudServer cloudServer;

    public BukkitCloudServerInitEvent(CloudServer cloudServer) {
        super();
        this.cloudServer = cloudServer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    /**
     * @return the server that has been initialized.
     */
    public CloudServer getCloudServer() {
        return cloudServer;
    }
}
