/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by Tareko on 15.10.2017.
 */
public class BungeeCordChannelExample {

    public void sendToaRandomServerInGroup(Plugin plugin, Player player) {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "cloudnet:main");
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("Connect");
        byteArrayDataOutput.writeUTF("Lobby"); //Connect to the group Lobby
        player.sendPluginMessage(plugin, "cloudnet:main", byteArrayDataOutput.toByteArray());
    }

    public void sendToFallback(Plugin plugin, Player player) {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "cloudnet:main");
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("Fallback"); //Connect to the fallback server in the iteration
        player.sendPluginMessage(plugin, "cloudnet:main", byteArrayDataOutput.toByteArray());
    }

}
