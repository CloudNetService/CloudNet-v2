/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import jline.console.ConsoleReader;

/**
 * Interface for classes that launch a setup sequence
 *
 * @see Setup
 */
public interface ISetup {

	/**
	 * Starts the setup sequence.
	 *
	 * @param consoleReader the console to read user input from
	 */
	void start(ConsoleReader consoleReader);

}
