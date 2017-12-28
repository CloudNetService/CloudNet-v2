/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.sender;

import de.dytanic.cloudnet.lib.network.protocol.IProtocol;

/**
 * Created by Tareko on 09.09.2017.
 */
public interface IProtocolSender {

    void send(Object object);

    void sendSynchronized(Object object);

    void sendAsynchronized(Object object);

    void send(IProtocol iProtocol, Object element);

    void send(int id, Object element);

    void sendAsynchronized(int id, Object element);

    void sendAsynchronized(IProtocol iProtocol, Object element);

    void sendSynchronized(int id, Object element);

    void sendSynchronized(IProtocol iProtocol, Object element);

}