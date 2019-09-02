/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.event.server.ScreenInfoEvent;
import de.dytanic.cloudnetcore.network.components.INetworkComponent;

import java.lang.reflect.Type;
import java.util.Collection;

public final class PacketInSendScreenLine extends PacketInHandler {

    private static final Type TYPE = new TypeToken<Collection<ScreenInfo>>() {}.getType();

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Collection<ScreenInfo> screenInfos = data.getObject("screenInfo", TYPE);
        CloudNet.getInstance().getEventManager().callEvent(new ScreenInfoEvent(screenInfos));

        for (ScreenInfo screenInfo : screenInfos) {
            if (CloudNet.getInstance().getScreenProvider().getMainServiceId() != null && screenInfo.getServiceId()
                                                                                                   .getServerId()
                                                                                                   .equalsIgnoreCase(CloudNet.getInstance()
                                                                                                                             .getScreenProvider()
                                                                                                                             .getMainServiceId()
                                                                                                                             .getServerId())) {
                System.out.println('[' + screenInfo.getServiceId().getServerId() + "] " + screenInfo.getLine());
            }
        }

        CloudNet.getInstance().getNetworkManager().handleScreen(((INetworkComponent) packetSender), screenInfos);
    }
}
