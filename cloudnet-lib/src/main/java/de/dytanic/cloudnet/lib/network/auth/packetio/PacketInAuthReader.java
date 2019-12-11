package de.dytanic.cloudnet.lib.network.auth.packetio;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.auth.Auth;
import de.dytanic.cloudnet.lib.network.auth.AuthType;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;

/**
 * Created by Tareko on 22.07.2017.
 */
public abstract class PacketInAuthReader implements PacketInHandler {

    public static final Type AUTH_TYPE = TypeToken.get(Auth.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        Auth auth = packet.getData().getObject("auth", AUTH_TYPE);
        handleAuth(auth, auth.getType(), auth.getAuthData(), packetSender);
    }

    public abstract void handleAuth(Auth auth, AuthType authType, Document authData, PacketSender packetSender);

}
