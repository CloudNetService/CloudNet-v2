/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Calls if a Player loguts from the proxy
 */
@Getter
@AllArgsConstructor
public class LogoutEvent extends Event {

    private CloudPlayer playerWhereAmI;

}