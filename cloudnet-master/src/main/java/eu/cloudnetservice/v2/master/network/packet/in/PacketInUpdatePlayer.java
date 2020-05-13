package eu.cloudnetservice.v2.master.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.lib.player.OfflinePlayer;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.packet.out.PacketOutUpdateOfflinePlayer;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketInUpdatePlayer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        OfflinePlayer offlinePlayer = packet.getData().getObject("player", OfflinePlayer.TYPE);
        CloudNet.getInstance().getDbHandlers().getPlayerDatabase().updatePlayer(offlinePlayer);

        if (CloudNet.getInstance().getNetworkManager().getOnlinePlayers().containsKey(offlinePlayer.getUniqueId())) {
            CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager().getOnlinePlayers().get(offlinePlayer.getUniqueId());
            cloudPlayer.setMetaData(offlinePlayer.getMetaData());
            CloudNet.getInstance().getNetworkManager().handlePlayerUpdate(cloudPlayer);
        } else {
            CloudNet.getInstance().getNetworkManager().sendAllUpdate(new PacketOutUpdateOfflinePlayer(offlinePlayer));
        }
    }
}
