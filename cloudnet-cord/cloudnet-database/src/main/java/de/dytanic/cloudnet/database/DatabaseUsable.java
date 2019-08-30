/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.database;

import de.dytanic.cloudnet.lib.database.Database;

/**
 * Parent class for all databases used by CloudNet.
 */
public class DatabaseUsable {

    /**
     * The data store that this database uses
     */
    protected Database database;

    public DatabaseUsable(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }
}
