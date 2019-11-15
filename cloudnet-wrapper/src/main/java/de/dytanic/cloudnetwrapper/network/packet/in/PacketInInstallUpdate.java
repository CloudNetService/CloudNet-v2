/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;

import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PacketInInstallUpdate implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        try {
            URLConnection url = new URL(packet.getData().getString("url")).openConnection();
            url.connect();
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Files.copy(url.getInputStream(), Paths.get("CloudNet-Wrapper-" + NetworkUtils.RANDOM.nextLong() + ".jar"));
            } else {
                Files.copy(url.getInputStream(), Paths.get("CloudNet-Wrapper.jar"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
