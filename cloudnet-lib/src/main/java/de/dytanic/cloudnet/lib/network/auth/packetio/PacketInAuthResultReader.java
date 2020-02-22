package de.dytanic.cloudnet.lib.network.auth.packetio;

import de.dytanic.cloudnet.lib.network.auth.AuthLoginResult;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;

/**
 * Created by Tareko on 25.07.2017.
 */
public abstract class PacketInAuthResultReader implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        AuthLoginResult authLoginResult = packet.getData().getObject("result", AuthLoginResult.class);
        handleResult(authLoginResult);
    }

    public abstract void handleResult(AuthLoginResult authLoginResult);

}
