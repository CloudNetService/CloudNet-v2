package de.dytanic.cloudnet.api.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;

import java.lang.reflect.Type;

/**
 * Created by Tareko on 17.08.2017.
 */
public class PacketInCloudNetwork implements PacketInHandlerDefault {

    private static final Type CLOUDNET_TYPE = TypeToken.get(CloudNetwork.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (CloudAPI.getInstance() != null) {
            CloudNetwork cloudNetwork = packet.getData().getObject("cloudnetwork", CLOUDNET_TYPE);
            CloudAPI.getInstance().setCloudNetwork(cloudNetwork);
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(
                obj -> obj.onCloudNetworkUpdate(cloudNetwork));
        }
    }
}
