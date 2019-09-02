/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

/**
 * Interface for classes that execute commands.
 */
public interface CommandExecutor {
	/**
	 * Method that is called when a command should execute.
	 *
	 * @param sender the sender that dispatched the execution of the command
	 * @param args   the arguments that the command was called with
	 */
	void onExecuteCommand(CommandSender sender, String[] args);
}
