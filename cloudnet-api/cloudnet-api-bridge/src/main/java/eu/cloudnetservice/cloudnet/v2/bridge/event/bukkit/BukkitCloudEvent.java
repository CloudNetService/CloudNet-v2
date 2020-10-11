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

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

/**
 * This class defines an abstract Bukkit CloudNet Event.
 * All events called in context of CloudNet are derived from this class.
 * These events are only ever called on Bukkit servers.
 * <p>
 * There are no guarantees about the synchronicity of the events.
 * Use {@link Event#isAsynchronous()} to check for asynchronous events.
 */
public abstract class BukkitCloudEvent extends Event {

    /**
     * Creates a new event.
     * This event's asynchronous property is determined by whether or not is it constructed
     * on the main thread.
     */
    public BukkitCloudEvent() {
        super(!Bukkit.isPrimaryThread());
    }

}
