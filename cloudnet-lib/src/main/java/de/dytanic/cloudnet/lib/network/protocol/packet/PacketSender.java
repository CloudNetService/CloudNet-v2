/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.network.protocol.sender.IProtocolSender;

/**
 * Created by Tareko on 24.05.2017.
 */
public interface PacketSender extends Nameable, IProtocolSender {

    void sendPacket(Packet... packets);

    void sendPacket(Packet packet);

    void sendPacketSynchronized(Packet packet);

}
