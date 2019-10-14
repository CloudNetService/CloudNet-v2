/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutCreateTemplate extends Packet {

    public PacketOutCreateTemplate(ServerGroup serverGroup) {
        super(PacketRC.CN_CORE + 5, new Document("serverGroup", serverGroup).append("type", DefaultType.BUKKIT.name()));
    }

    public PacketOutCreateTemplate(ProxyGroup serverGroup) {
        super(PacketRC.CN_CORE + 5, new Document("proxyGroup", serverGroup).append("type", DefaultType.BUNGEE_CORD.name()));
    }
}
