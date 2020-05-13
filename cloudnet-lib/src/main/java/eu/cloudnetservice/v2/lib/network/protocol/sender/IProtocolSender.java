package eu.cloudnetservice.v2.lib.network.protocol.sender;

import eu.cloudnetservice.v2.lib.network.protocol.IProtocol;

/**
 * Created by Tareko on 09.09.2017.
 */
public interface IProtocolSender {

    void send(Object object);

    void sendSynchronized(Object object);

    void sendAsynchronous(Object object);

    void send(IProtocol iProtocol, Object element);

    void send(int id, Object element);

    void sendAsynchronous(int id, Object element);

    void sendAsynchronous(IProtocol iProtocol, Object element);

    void sendSynchronized(int id, Object element);

    void sendSynchronized(IProtocol iProtocol, Object element);

}
