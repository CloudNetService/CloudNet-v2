/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PacketInInstallUpdate extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        try {
            URLConnection url = new java.net.URL(data.getString("url")).openConnection();
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
