package de.dytanic.cloudnetcore.mobs.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.mobs.MobModule;

/**
 * Created by Tareko on 01.09.2017.
 */
public class PacketInRemoveMob implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServerMob serverMob = packet.getData().getObject("mob", ServerMob.TYPE);
        MobModule.getInstance().getMobDatabase().remove(serverMob);
        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll();
    }
}
