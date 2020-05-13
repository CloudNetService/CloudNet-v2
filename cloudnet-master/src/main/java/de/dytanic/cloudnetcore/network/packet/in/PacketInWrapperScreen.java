package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.service.wrapper.WrapperScreen;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

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
