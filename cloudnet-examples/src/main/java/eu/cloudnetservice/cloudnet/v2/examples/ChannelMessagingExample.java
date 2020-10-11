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

package eu.cloudnetservice.cloudnet.v2.examples;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.out.PacketOutCustomChannelMessage;
import eu.cloudnetservice.cloudnet.v2.bridge.event.proxied.ProxiedSubChannelMessageEvent;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import net.md_5.bungee.event.EventHandler;

public class ChannelMessagingExample {

    public void sendCustomMessage() {
        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutCustomChannelMessage("some-sub-channel-for-proxy",
                                                                                                   "handle",
                                                                                                   new Document("foo",
                                                                                                                "bar"))); //send a custom channel message to all
        CloudAPI.getInstance().sendCustomSubProxyMessage("some-sub-channel-for-proxy",
                                                         "handle",
                                                         new Document("foo", "bar")); //send a custom channel message to all proxys
    }

    @EventHandler
    public void channelSubReceive(ProxiedSubChannelMessageEvent e) //handle the received channel message
    {
        if (e.getChannel().equalsIgnoreCase("some-sub-channel-for-proxy")) {
            if (e.getMessage().equalsIgnoreCase("handle")) {
                System.out.println(e.getDocument().convertToJson());
            }
        }
    }

}
