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

package eu.cloudnetservice.cloudnet.v2.master.player;

import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.player.PlayerExecutor;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

import java.util.Objects;

/**
 * CloudNet core implementation for the player executor.
 * This class is usable only with master plugins and allows developers to interact with players regardless
 * of their connected proxy.
 * <p>
 * Use the {@link #INSTANCE} to use this class.
 */
public final class CorePlayerExecutor extends PlayerExecutor {

    public static final PlayerExecutor INSTANCE = new CorePlayerExecutor();

    private CorePlayerExecutor() {
    }

    @Override
    public void sendPlayer(CloudPlayer cloudPlayer, String server) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(server, "the provided server is null");
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendPlayer",
                                                                    new Document("uniqueId", cloudPlayer.getUniqueId())
                                                                        .append("name", cloudPlayer.getName())
                                                                        .append("server", server));
    }

    @Override
    public void kickPlayer(CloudPlayer cloudPlayer, String reason) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(reason, "the provided reason is null");
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "kickPlayer",
                                                                    new Document("uniqueId", cloudPlayer.getUniqueId())
                                                                        .append("name", cloudPlayer.getName())
                                                                        .append("reason", reason));
    }

    @Override
    public void sendMessage(CloudPlayer cloudPlayer, String message) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(message, "the provided message is null");
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendMessage",
                                                                    new Document("message", message)
                                                                        .append("name", cloudPlayer.getName())
                                                                        .append("uniqueId", cloudPlayer.getUniqueId()));
    }

    @Override
    public void sendActionbar(final CloudPlayer cloudPlayer, final String message) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(message, "the provided message is null");
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendActionbar",
                                                                    new Document("message", message)
                                                                        .append("uniqueId", cloudPlayer.getUniqueId()));
    }

    @Override
    public void sendTitle(final CloudPlayer cloudPlayer,
                          final String title,
                          final String subTitle,
                          final int fadeIn,
                          final int stay,
                          final int fadeOut) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(title, "the provided title is null");
        Objects.requireNonNull(subTitle, "the provided subtitle is null");

        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendTitle",
                                                                    new Document("uniqueId", cloudPlayer.getUniqueId())
                                                                        .append("title", title)
                                                                        .append("subTitle", subTitle)
                                                                        .append("stay", stay)
                                                                        .append("fadeIn", fadeIn)
                                                                        .append("fadeOut", fadeOut));

    }
}
