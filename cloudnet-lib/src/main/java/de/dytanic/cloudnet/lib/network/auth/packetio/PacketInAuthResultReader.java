package de.dytanic.cloudnet.lib.network.auth.packetio;

import de.dytanic.cloudnet.lib.network.auth.AuthLoginResult;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 25.07.2017.
 */
public abstract class PacketInAuthResultReader extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        AuthLoginResult authLoginResult = data.getObject("result", AuthLoginResult.class);
        handleResult(authLoginResult);
    }

    public abstract void handleResult(AuthLoginResult authLoginResult);

}
