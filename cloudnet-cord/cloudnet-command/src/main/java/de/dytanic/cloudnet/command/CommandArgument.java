/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

/**
 * Class to process arguments before and after calling a command.
 */
public abstract class CommandArgument {

    /**
     * The name of the argument to process
     */
    private String name;

    public CommandArgument(String name) {
        this.name = name;
    }

    /**
     * Method to execute before an argument is processed.
     *
     * @param command     the command that is executed after all arguments are processed
     * @param commandLine the complete command line for this command
     */
    public abstract void preExecute(Command command, String commandLine);

    /**
     * Method to execute after a command is executed.
     *
     * @param command     the command that was executed before all arguments are processed
     * @param commandLine the complete command line for this command
     */
    public abstract void postExecute(Command command, String commandLine);

    public String getName() {
        return name;
    }
}
