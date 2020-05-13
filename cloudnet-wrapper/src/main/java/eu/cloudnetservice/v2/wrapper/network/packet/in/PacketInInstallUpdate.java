package eu.cloudnetservice.v2.wrapper.network.packet.in;

import eu.cloudnetservice.v2.lib.NetworkUtils;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;

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
