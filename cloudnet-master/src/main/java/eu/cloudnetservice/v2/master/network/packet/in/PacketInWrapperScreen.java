package eu.cloudnetservice.v2.master.network.packet.in;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.service.wrapper.WrapperScreen;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

import java.lang.reflect.Type;

/**
 * Created by Tareko on 23.09.2017.
 */
public class PacketInWrapperScreen implements PacketInHandler {

    private static final Type WRAPPER_SCREEN_TYPE = TypeToken.get(WrapperScreen.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        CloudNet.getInstance().getNetworkManager().handleWrapperScreenInput(
            (Wrapper) packetSender,
            packet.getData().getObject("screen", WRAPPER_SCREEN_TYPE));
    }
}
