package de.dytanic.cloudnetcore.network.packet.dbsync;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.lang.reflect.Type;

/**
 * Created by Tareko on 25.08.2017.
 */
public final class PacketDBInInsertDocument implements PacketInHandler {

    public static final Type DOCUMENT_ARRAY_TYPE = TypeToken.getArray(Document.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudNet.getInstance().getDatabaseManager().getDatabase(packet.getData().getString("db"))
                .insert(packet.getData().getObject("insert", DOCUMENT_ARRAY_TYPE));
    }
}
