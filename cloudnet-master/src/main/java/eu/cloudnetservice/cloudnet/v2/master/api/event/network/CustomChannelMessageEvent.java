/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.api.event.network;

import eu.cloudnetservice.cloudnet.v2.event.async.AsyncEvent;
import eu.cloudnetservice.cloudnet.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Calls if a CustomChannelMessage was received from a INetworkComponent
 */
public class CustomChannelMessageEvent extends AsyncEvent<CustomChannelMessageEvent> {

    private final String channel;

    private final String message;

    private final Document document;

    private final PacketSender packetSender;

    public CustomChannelMessageEvent(PacketSender packetSender, String channel, String message, Document document) {
        super(new AsyncPosterAdapter<>());
        this.channel = channel;
        this.message = message;
        this.document = document;
        this.packetSender = packetSender;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public Document getDocument() {
        return document;
    }

    public PacketSender getPacketSender() {
        return packetSender;
    }
}
