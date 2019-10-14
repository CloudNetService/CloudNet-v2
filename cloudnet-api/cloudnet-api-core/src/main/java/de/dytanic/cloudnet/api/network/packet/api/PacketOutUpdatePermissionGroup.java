/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutUpdatePermissionGroup extends Packet {

    public PacketOutUpdatePermissionGroup(PermissionGroup permissionGroup) {
        super(PacketRC.CN_CORE + 1, new Document("permissionGroup", permissionGroup));
    }
}
