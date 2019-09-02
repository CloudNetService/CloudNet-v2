/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Functional interface for methods that are called after a setup sequence
 * completes successfully.
 */
@Deprecated
public interface ISetupComplete {

	/**
	 * Method that is called after a setup sequence completed successfully.
	 *
	 * @param data the data that has been entered by the user.
	 */
	void complete(Document data);

}
