/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

/**
 * Functional interface for methods that are called when a setup sequence is
 * cancelled.
 *
 * @see ISetup
 */
@Deprecated
public interface ISetupCancel {
	/**
	 * Method to call when a setup sequence is cancelled.
	 */
	void cancel();
}
