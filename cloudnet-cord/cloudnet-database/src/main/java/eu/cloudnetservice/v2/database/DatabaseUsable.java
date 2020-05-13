package eu.cloudnetservice.v2.database;

import eu.cloudnetservice.v2.lib.database.Database;

/**
 * Parent class for all databases used by CloudNet.
 */
public abstract class DatabaseUsable {

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
