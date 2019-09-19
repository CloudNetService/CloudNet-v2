/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.network.components.Wrapper;

/**
 * Created by Tareko on 28.08.2017.
 */
public class PacketInUpdateCPUUsage extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }
        double cpuUsage = data.getDouble("cpuUsage");
        ((Wrapper) packetSender).setCpuUsage(cpuUsage);
    }
}
