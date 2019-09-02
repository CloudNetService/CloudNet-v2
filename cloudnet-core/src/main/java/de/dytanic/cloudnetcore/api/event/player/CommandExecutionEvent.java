/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnet.lib.player.PlayerCommandExecution;

/**
 * Calls if a command was executed from one player
 */
public class CommandExecutionEvent extends AsyncEvent<CommandExecutionEvent> {

    private PlayerCommandExecution playerCommandExecution;

    public CommandExecutionEvent(PlayerCommandExecution playerCommandExecution) {
        super(new AsyncPosterAdapter<>());
        this.playerCommandExecution = playerCommandExecution;
    }

    public PlayerCommandExecution getPlayerCommandExecution() {
        return playerCommandExecution;
    }
}
