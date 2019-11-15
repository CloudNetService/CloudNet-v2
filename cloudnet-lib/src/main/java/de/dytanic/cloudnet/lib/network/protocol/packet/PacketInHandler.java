/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.packet;

/**
 * Created by Tareko on 18.07.2017.
 */
public interface PacketInHandler {
    void handleInput(Packet packet, PacketSender packetSender);
}
