package eu.cloudnetservice.v2.master.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;

import java.util.UUID;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetOfflinePlayer implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packet.getData().contains("uniqueId")) {
            UUID uniqueId = packet.getData().getObject("uniqueId", UUID.class);

            OfflinePlayer offlinePlayer = CloudNet.getInstance()
                                                  .getNetworkManager()
                                                  .getOnlinePlayer(uniqueId); //use cache for offline player instance

            if (offlinePlayer == null) {
                offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
            }

            packetSender.sendPacket(getResult(packet, new Document("player", offlinePlayer)));
        } else {
            String name = packet.getData().getString("name");

            OfflinePlayer offlinePlayer = CloudNet.getInstance()
                                                  .getNetworkManager()
                                                  .getPlayer(name); //use cache for offline player instance

            if (offlinePlayer == null) {
                offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(
                    CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(name));
            }

            packetSender.sendPacket(getResult(packet, new Document("player", offlinePlayer)));
        }
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.PLAYER_HANDLE, value);
    }
}
