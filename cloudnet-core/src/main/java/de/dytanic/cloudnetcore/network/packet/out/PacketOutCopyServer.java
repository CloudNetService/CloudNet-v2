/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 28.08.2017.
 */
public class PacketOutCopyServer extends Packet {

    public PacketOutCopyServer(ServerInfo serverInfo) {
        super(PacketRC.CN_CORE + 10, new Document("serverInfo", serverInfo));
    }

    public PacketOutCopyServer(ServerInfo serverInfo, Template template) {
        super(PacketRC.CN_CORE + 10, new Document("serverInfo", serverInfo).append("template", template));
    }
}
