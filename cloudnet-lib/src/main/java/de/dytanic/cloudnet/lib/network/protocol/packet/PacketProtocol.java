/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet;

import de.dytanic.cloudnet.lib.network.protocol.IProtocol;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolStream;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Tareko on 09.09.2017.
 */
public class PacketProtocol implements IProtocol {

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public Collection<Class<?>> getAvailableClasses() {
        return Arrays.asList(Packet.class);
    }

    @Override
    public ProtocolStream createElement(Object element) {
        if (element instanceof Packet) {
            return (Packet) element;
        }
        return null;
    }

    @Override
    public ProtocolStream createEmptyElement() {
        return new Packet();
    }
}
