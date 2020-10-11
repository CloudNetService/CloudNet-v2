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

package eu.cloudnetservice.cloudnet.v2.master.network.packet.in;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.screen.ScreenInfo;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.api.event.server.ScreenInfoEvent;
import eu.cloudnetservice.cloudnet.v2.master.network.components.INetworkComponent;

import java.lang.reflect.Type;
import java.util.Collection;

public final class PacketInSendScreenLine implements PacketInHandler {

    private static final Type COLLECTION_SCREEN_INFO_TYPE = TypeToken.getParameterized(Collection.class, ScreenInfo.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        Collection<ScreenInfo> screenInfos = packet.getData().getObject("screenInfo", COLLECTION_SCREEN_INFO_TYPE);
        CloudNet.getInstance().getEventManager().callEvent(new ScreenInfoEvent(screenInfos));

        for (ScreenInfo screenInfo : screenInfos) {
            if (CloudNet.getInstance().getScreenProvider().getMainServiceId() != null &&
                screenInfo.getServiceId().getServerId().equalsIgnoreCase(
                    CloudNet.getInstance().getScreenProvider().getMainServiceId().getServerId())) {
                System.out.println('[' + screenInfo.getServiceId().getServerId() + "] " + screenInfo.getLine());
            }
        }

        CloudNet.getInstance().getNetworkManager().handleScreen(((INetworkComponent) packetSender), screenInfos);
    }
}
