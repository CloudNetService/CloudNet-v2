package eu.cloudnetservice.cloudnet.v2.lib.network.protocol;

import java.util.Collection;

/**
 * Created by Tareko on 09.09.2017.
 */
public interface IProtocol {

    int getId();

    Collection<Class<?>> getAvailableClasses();

    ProtocolStream createElement(Object element) throws Exception;

    ProtocolStream createEmptyElement();

}