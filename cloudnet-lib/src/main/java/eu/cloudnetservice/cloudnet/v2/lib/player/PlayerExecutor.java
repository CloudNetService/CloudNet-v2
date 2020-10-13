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

package eu.cloudnetservice.cloudnet.v2.lib.player;

/**
 * Player executors allow developers to interact with players across the entire CloudNet network.
 * These actions succeed, as long as the player is connected to any proxy.
 */
@SuppressWarnings("unused")
public class PlayerExecutor {

    /**
     * Sends the player to another server.
     * If the server is not connected to the proxy the player is connected to, this method may not cause any action to happen.
     *
     * @param cloudPlayer the player to send to the given server.
     * @param server      the server-id to send the player to.
     */
    public void sendPlayer(CloudPlayer cloudPlayer, String server) {
    }

    /**
     * Kicks the player with the given reason from the network.
     *
     * @param cloudPlayer the player to kick.
     * @param reason      the reason to display the player that is kicked.
     */
    public void kickPlayer(CloudPlayer cloudPlayer, String reason) {
    }

    /**
     * Sends a simple legacy text message to the given player.
     *
     * @param cloudPlayer the player to send the message to.
     * @param message     the message to send.
     */
    public void sendMessage(CloudPlayer cloudPlayer, String message) {
    }

    /**
     * Displays an action bar for the given player.
     * This is a special type of chat message, that appears on top of the item names.
     *
     * @param cloudPlayer the player to send the action bar to.
     * @param message     the message to send to the player.
     */
    public void sendActionbar(CloudPlayer cloudPlayer, String message) {
    }

    /**
     * Sends a title to the given player.
     * All messages are treated as legacy messages.
     *
     * @param cloudPlayer the player to send the title to.
     * @param title       the legacy title message.
     * @param subTitle    the legacy subtitle message.
     * @param fadeIn      time in ticks for the title to fade in.
     * @param stay        time in ticks the title should stay.
     * @param fadeOut     time in ticks for the title to fade out.
     */
    public void sendTitle(CloudPlayer cloudPlayer, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
    }

}
