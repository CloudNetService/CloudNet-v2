package de.dytanic.cloudnet.lib.network.auth.packetio;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.auth.Auth;
import de.dytanic.cloudnet.lib.network.auth.AuthType;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 22.07.2017.
 */
public abstract class PacketInAuthReader extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Auth auth = data.getObject("auth", new TypeToken<Auth>() {}.getType());
        handleAuth(auth, auth.getType(), auth.getAuthData(), packetSender);
    }

    public abstract void handleAuth(Auth auth, AuthType authType, Document authData, PacketSender packetSender);

}
