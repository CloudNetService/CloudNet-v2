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
