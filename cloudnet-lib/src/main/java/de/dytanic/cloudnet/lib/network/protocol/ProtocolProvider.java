/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.file.FileProtocol;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketProtocol;
import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Map;


/**
 * Created by Tareko on 09.09.2017.
 */
public final class ProtocolProvider {

    private static Map<Integer, IProtocol> protocols;

    static {
        protocols = NetworkUtils.newConcurrentHashMap();
        registerProtocol(new PacketProtocol());
        registerProtocol(new FileProtocol());
    }

    private ProtocolProvider() {
    }

    public static ProtocolBuffer protocolBuffer(ByteBuf byteBuf) {
        return new ProtocolBuffer(byteBuf);
    }

    public static void registerProtocol(IProtocol iProtocol) {
        protocols.put(iProtocol.getId(), iProtocol);
    }

    public static IProtocol getProtocol(int id) {
        return protocols.get(id);
    }

    public static Collection<IProtocol> protocols() {
        return protocols.values();
    }
}
