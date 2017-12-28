/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.player;

        import lombok.Getter;

@Getter
public class PlayerExecutor {

    protected boolean available = false;

    public void sendPlayer(CloudPlayer cloudPlayer, String server) {}

    public void kickPlayer(CloudPlayer cloudPlayer, String reason) {}

    public void sendMessage(CloudPlayer cloudPlayer, String message) {}

}