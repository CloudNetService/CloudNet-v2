package eu.cloudnetservice.v2.master.api.event.player;

import eu.cloudnetservice.v2.event.async.AsyncEvent;
import eu.cloudnetservice.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.v2.lib.player.PlayerCommandExecution;

/**
 * Calls if a command was executed from one player
 */
public class CommandExecutionEvent extends AsyncEvent<CommandExecutionEvent> {

    private final PlayerCommandExecution playerCommandExecution;

    public CommandExecutionEvent(PlayerCommandExecution playerCommandExecution) {
        super(new AsyncPosterAdapter<>());
        this.playerCommandExecution = playerCommandExecution;
    }

    public PlayerCommandExecution getPlayerCommandExecution() {
        return playerCommandExecution;
    }
}
