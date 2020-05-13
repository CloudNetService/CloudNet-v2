package eu.cloudnetservice.v2.lib.player;

/**
 * Created by Tareko on 23.07.2017.
 */
public class PlayerCommandExecution {

    private final String name;

    private final String commandLine;

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