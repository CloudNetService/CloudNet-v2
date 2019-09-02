/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.in;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;

/**
 * Created by Tareko on 17.08.2017.
 */
public class PacketInCloudNetwork extends PacketInHandlerDefault {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (CloudAPI.getInstance() != null) {
            CloudNetwork cloudNetwork = data.getObject("cloudnetwork", NetworkUtils.cloudnet().getType());
            CloudAPI.getInstance().setCloudNetwork(cloudNetwork);
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(new Runnabled<NetworkHandler>() {
                @Override
                public void run(NetworkHandler obj) {
                    obj.onCloudNetworkUpdate(cloudNetwork);
                }
            });
        }
    }
}
