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
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * This event is called when the proxy requests or searches for a new server for
 * a given player to connect to.
 * This event can be used to set a custom or specific server.
 */
public class ProxiedPlayerFallbackEvent extends ProxiedCloudEvent {

    private final ProxiedPlayer proxiedPlayer;

    private final CloudPlayer cloudPlayer;

    private final FallbackType fallbackType;

    private String fallback;

    public ProxiedPlayerFallbackEvent(ProxiedPlayer proxiedPlayer, CloudPlayer cloudPlayer, FallbackType fallbackType, String fallback) {
        this.proxiedPlayer = proxiedPlayer;
        this.cloudPlayer = cloudPlayer;
        this.fallbackType = fallbackType;
        this.fallback = fallback;
    }

    /**
     * @return the server-id of the server the player should be connected to.
     */
    public String getFallback() {
        return fallback;
    }

    /**
     * @param fallback the server-id of the server the player should connect to;
     *                 set to {@code null} to prevent a player to be connected to any server.
     */
    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    /**
     * @return the player connected and registered in CloudNet that a fallback server is requested for.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    /**
     * @return the cause of this fallback request.
     */
    public FallbackType getFallbackType() {
        return fallbackType;
    }

    /**
     * @return the player connected to this proxy server.
     */
    public ProxiedPlayer getProxiedPlayer() {
        return proxiedPlayer;
    }

    /**
     * Enumeration of causes for a fallback request event.
     */
    public enum FallbackType {

        /**
         * The player has issued the /hub command.
         */
        HUB_COMMAND,
        /**
         * The player has been kicked from the last server they were connected to.
         */
        SERVER_KICK,
        /**
         * The player has connected and has yet to be connected to a server.
         */
        SERVER_CONNECT,
        /**
         * A custom cause for a fallback request.
         */
        CUSTOM

    }

}
