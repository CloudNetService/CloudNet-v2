package eu.cloudnetservice.v2.master.network.components.util;

import eu.cloudnetservice.v2.master.network.components.INetworkComponent;

/**
 * Created by Tareko on 20.07.2017.
 */
public interface ChannelFilter {

    boolean accept(INetworkComponent networkComponent);

}