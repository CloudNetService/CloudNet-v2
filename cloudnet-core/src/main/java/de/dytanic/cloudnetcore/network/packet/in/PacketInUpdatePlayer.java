/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdateOfflinePlayer;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketInUpdatePlayer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        OfflinePlayer offlinePlayer = packet.getData().getObject("player", OfflinePlayer.TYPE);
        CloudNet.getInstance().getDbHandlers().getPlayerDatabase().updatePlayer(offlinePlayer);

        if (CloudNet.getInstance().getNetworkManager().getOnlinePlayers().containsKey(offlinePlayer.getUniqueId())) {
            CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager().getOnlinePlayers().get(offlinePlayer.getUniqueId());
            cloudPlayer.setMetaData(offlinePlayer.getMetaData());
            cloudPlayer.setPermissionEntity(offlinePlayer.getPermissionEntity());
            CloudNet.getInstance().getNetworkManager().handlePlayerUpdate(cloudPlayer);
        } else {
            CloudNet.getInstance().getNetworkManager().sendAllUpdate(new PacketOutUpdateOfflinePlayer(offlinePlayer));
        }
    }
}
