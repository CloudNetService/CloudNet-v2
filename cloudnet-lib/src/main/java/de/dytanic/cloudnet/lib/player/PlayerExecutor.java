/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.player;

import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;

@Getter
public class PlayerExecutor {

    protected boolean available = false;

    public void sendPlayer(CloudPlayer cloudPlayer, String server)
    {
    }

    public void kickPlayer(CloudPlayer cloudPlayer, String reason)
    {
    }

    public void sendMessage(CloudPlayer cloudPlayer, String message)
    {
    }

    public void sendActionbar(CloudPlayer cloudPlayer, String message)
    {
    }

    public void sendTitle(CloudPlayer cloudPlayer, String title, String subTitle, int fadeIn, int stay, int fadeOut)
    {
    }

    public void broadcastMessage(String message)
    {
    }

    public void broadcastMessage(String message, String permission)
    {
    }

    public void broadcastMessage(BaseComponent[] baseComponents)
    {
    }

    public void broadcastMessage(BaseComponent[] baseComponents, String permission)
    {
    }

}