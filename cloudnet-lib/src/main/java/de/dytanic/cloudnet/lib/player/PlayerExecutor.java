/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.player;

public class PlayerExecutor {

    protected boolean available = false;

    public boolean isAvailable() {
        return available;
    }

    public void sendPlayer(CloudPlayer cloudPlayer, String server) {
    }

    public void kickPlayer(CloudPlayer cloudPlayer, String reason) {
    }

    public void sendMessage(CloudPlayer cloudPlayer, String message) {
    }

    public void sendActionbar(CloudPlayer cloudPlayer, String message) {
    }

    public void sendTitle(CloudPlayer cloudPlayer, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
    }

}
