package eu.cloudnetservice.cloudnet.v2.api.database;

import eu.cloudnetservice.cloudnet.v2.lib.database.Database;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Tareko on 20.08.2017.
 */
public class DatabaseManager {

    private final Map<String, Database> databaseMap = new ConcurrentHashMap<>();

    public DatabaseManager() {

    }

    public Collection<Database> getCachedDatabases() {
        return databaseMap.values();
    }

    public Database getDatabase(String name) {
        return databaseMap.computeIfAbsent(name, DatabaseImpl::new);
    }
}
