/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.in;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.function.Consumer;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketInCustomChannelMessage extends PacketInHandlerDefault {

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        if (CloudAPI.getInstance() != null)
        {
            CloudAPI.getInstance().getNetworkHandlerProvider().iterator(new Consumer<NetworkHandler>() {
                @Override
                public void accept(NetworkHandler obj)
                {
                    obj.onCustomChannelMessageReceive(data.getString("channel"), data.getString("message"), data.getDocument("value"));
                }
            });
        }
    }
}