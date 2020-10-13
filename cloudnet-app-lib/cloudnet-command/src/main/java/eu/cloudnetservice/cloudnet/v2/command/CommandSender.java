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

package eu.cloudnetservice.cloudnet.v2.command;

import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;

/**
 * Interface for denoting classes that can dispatch commands.
 */
public interface CommandSender extends Nameable {

    /**
     * Send messages to this command sender.
     *
     * @param message the messages to send
     */
    void sendMessage(String... message);

    /**
     * Query this command sender for its permissions.
     *
     * @param permission the permission in question
     *
     * @return {@code true} when this command sender has the queried permission,
     * {@code false} otherwise.
     */
    boolean hasPermission(String permission);

}
