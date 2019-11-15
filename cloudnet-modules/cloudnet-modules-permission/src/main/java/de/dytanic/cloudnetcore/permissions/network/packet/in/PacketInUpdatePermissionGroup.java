/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.permissions.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.permissions.PermissionModule;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInUpdatePermissionGroup implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        PermissionGroup permissionGroup = packet.getData().getObject("permissionGroup", PermissionGroup.TYPE);
        PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll0();
    }
}
