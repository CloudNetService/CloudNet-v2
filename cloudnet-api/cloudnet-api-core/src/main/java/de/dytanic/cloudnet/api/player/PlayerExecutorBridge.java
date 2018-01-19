/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.player;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 27.08.2017.
 */
public class PlayerExecutorBridge extends PlayerExecutor {

    public static final PlayerExecutorBridge INSTANCE = new PlayerExecutorBridge();

    public PlayerExecutorBridge()
    {
        this.available = true;
    }

    @Override
    public void kickPlayer(CloudPlayer cloudPlayer, String reason)
    {
        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "kickPlayer",
                new Document("uniqueId", cloudPlayer.getUniqueId()).append("name", cloudPlayer.getName()).append("reason", reason));
    }

    @Override
    public void sendPlayer(CloudPlayer cloudPlayer, String server)
    {
        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "sendPlayer",
                new Document("uniqueId", cloudPlayer.getUniqueId()).append("name", cloudPlayer.getName()).append("server", server));
    }

    @Override
    public void sendMessage(CloudPlayer cloudPlayer, String message)
    {
        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "sendMessage",
                new Document("message", message).append("name", cloudPlayer.getName()).append("uniqueId", cloudPlayer.getUniqueId()));
    }

}