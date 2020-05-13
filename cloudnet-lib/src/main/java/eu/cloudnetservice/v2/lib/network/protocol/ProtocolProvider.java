package eu.cloudnetservice.v2.lib.network.protocol;

import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketProtocol;
import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Tareko on 09.09.2017.
 */
public final class ProtocolProvider {

    private static final Map<Integer, IProtocol> protocols;

    static {
        protocols = new ConcurrentHashMap<>();
        registerProtocol(new PacketProtocol());
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