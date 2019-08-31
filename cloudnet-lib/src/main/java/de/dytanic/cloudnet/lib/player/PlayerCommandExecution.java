package de.dytanic.cloudnet.lib.player;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PlayerCommandExecution {

    private String name;

    private String commandLine;

    public PlayerCommandExecution(String name, String commandLine) {
        this.name = name;
        this.commandLine = commandLine;
    }

    public String getName() {
        return name;
    }

    public String getCommandLine() {
        return commandLine;
    }
}