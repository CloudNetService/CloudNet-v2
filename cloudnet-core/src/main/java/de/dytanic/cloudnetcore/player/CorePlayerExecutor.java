/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.player;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 10.09.2017.
 */
public class CorePlayerExecutor extends PlayerExecutor {

    public static final PlayerExecutor INSTANCE = new CorePlayerExecutor();

    @Override
    public void sendPlayer(CloudPlayer cloudPlayer, String server) {
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendPlayer", new Document("uniqueId",
                                                                                                                    cloudPlayer.getUniqueId())
            .append("name", cloudPlayer.getName())
            .append("server", server));
    }

    @Override
    public void kickPlayer(CloudPlayer cloudPlayer, String reason) {
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "kickPlayer", new Document("uniqueId",
                                                                                                                    cloudPlayer.getUniqueId())
            .append("name", cloudPlayer.getName())
            .append("reason", reason));
    }

    @Override
    public void sendMessage(CloudPlayer cloudPlayer, String message) {
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendMessage", new Document("message",
                                                                                                                     message).append("name",
                                                                                                                                     cloudPlayer
                                                                                                                                         .getName())
                                                                                                                             .append(
                                                                                                                                 "uniqueId",
                                                                                                                                 cloudPlayer
                                                                                                                                     .getUniqueId()));
    }
}
