/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnetcore.network.NetworkManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Calls if the NetworkManager.updateAll(); method is invoked
 */
@Getter
@AllArgsConstructor
public class UpdateAllEvent extends Event {

    private NetworkManager networkManager;

    private boolean isOnlineCloudNetworkUpdate;

}