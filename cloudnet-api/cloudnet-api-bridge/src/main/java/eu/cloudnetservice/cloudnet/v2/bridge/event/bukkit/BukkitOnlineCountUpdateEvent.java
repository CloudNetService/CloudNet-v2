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

import org.bukkit.event.HandlerList;

/**
 * The event is called whenever the amount of players currently online changes.
 * This change can be due to a login, logout or any other connection state change.
 */
public class BukkitOnlineCountUpdateEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final int onlineCount;

    public BukkitOnlineCountUpdateEvent(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the most recent online count.
     */
    public int getOnlineCount() {
        return onlineCount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
