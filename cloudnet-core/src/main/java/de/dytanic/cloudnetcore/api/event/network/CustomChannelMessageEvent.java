/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.Getter;

/**
 * Calls if a CustomChannelMessage was received from a INetworkComponent
 */
@Getter
public class CustomChannelMessageEvent extends AsyncEvent<CustomChannelMessageEvent> {

    private String channel;

    private String message;

    private Document document;

    private PacketSender packetSender;

    public CustomChannelMessageEvent(PacketSender packetSender, String channel, String message, Document document)
    {
        super(new AsyncPosterAdapter<>());
        this.channel = channel;
        this.message = message;
        this.document = document;
        this.packetSender = packetSender;
    }

}