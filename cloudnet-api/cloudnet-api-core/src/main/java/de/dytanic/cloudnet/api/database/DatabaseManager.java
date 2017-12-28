/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.database;

import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.utility.threading.Scheduler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 20.08.2017.
 */
public class DatabaseManager {

    private Map<String, Database> databaseMap = new ConcurrentHashMap<>();

    public DatabaseManager(Scheduler scheduler)
    {
        scheduler.runTaskRepeatSync(new Runnable() {
            @Override
            public void run()
            {
                databaseMap.clear();
            }
        }, 0, 1000);
    }

    public Collection<Database> getCachedDatabases()
    {
        return databaseMap.values();
    }

    public Database getDatabase(String name)
    {
        return new DatabaseImpl(name);
    }
}