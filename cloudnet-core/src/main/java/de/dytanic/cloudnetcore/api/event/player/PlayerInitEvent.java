/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 17.10.2017.
 */
@Getter
@AllArgsConstructor
public class PlayerInitEvent extends Event {

    private CloudPlayer cloudPlayer;

}