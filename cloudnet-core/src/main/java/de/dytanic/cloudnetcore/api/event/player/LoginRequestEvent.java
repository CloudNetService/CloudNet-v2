/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.Cancelable;
import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import lombok.*;

import java.util.UUID;

/**
 * Created by Tareko on 27.07.2017.
 */
@Getter
@AllArgsConstructor
@ToString
public class LoginRequestEvent extends Event implements Cancelable
{
    private UUID uniqueId;
    private PlayerConnection playerConnection;
    @Setter
    private boolean cancelled = false;
}