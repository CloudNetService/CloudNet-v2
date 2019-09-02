/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components.util;

import de.dytanic.cloudnetcore.network.components.INetworkComponent;

/**
 * Created by Tareko on 20.07.2017.
 */
public interface ChannelFilter {

	boolean accept(INetworkComponent networkComponent);

}