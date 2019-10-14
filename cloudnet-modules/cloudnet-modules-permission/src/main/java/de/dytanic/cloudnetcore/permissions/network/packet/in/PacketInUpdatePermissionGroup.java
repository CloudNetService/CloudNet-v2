/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.permissions.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.permissions.PermissionModule;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInUpdatePermissionGroup extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        PermissionGroup permissionGroup = data.getObject("permissionGroup", new TypeToken<PermissionGroup>() {}.getType());
        PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll0();
    }
}
