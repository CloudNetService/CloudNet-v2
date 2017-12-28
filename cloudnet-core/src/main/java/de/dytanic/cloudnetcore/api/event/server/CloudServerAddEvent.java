/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.server;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 25.10.2017.
 */
@Getter
@AllArgsConstructor
public class CloudServerAddEvent extends Event {

    private CloudServer cloudServer;

}