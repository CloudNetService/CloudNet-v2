/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.database;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.database.Database;

import java.io.File;
import java.util.*;


/**
 * Manager for a {@link Database}.
 * Saves all databases every 60 seconds and
 * clears the currently open databases every 6 minutes.
 */
public class DatabaseManager {

    private final File dir;
    private final Timer timer;
    private short tick = 1;

    private java.util.Map<String, Database> databaseCollection = NetworkUtils.newConcurrentHashMap();

    /**
     * Constructs a new database manager.
     */
    public DatabaseManager() {
        dir = new File("database");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdir();

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                save();
            }
        }, 0, 60000);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                save().clear();
            }
        }, 0, 360000);
    }

    /**
     * Saves the currently opened documents in the loaded databases.
     *
     * @return this manager for chaining
     *
     * @see DatabaseImpl#save()
     */
    public DatabaseManager save() {
        for (Database database : databaseCollection.values()) {
            ((DatabaseImpl) database).save();
        }
        return this;
    }

    /**
     * Clears the currently opened documents of the loaded databases.
     *
     * @return this manager for chaining
     *
     * @see DatabaseImpl#clear()
     */
    public DatabaseManager clear() {
        for (Database database : databaseCollection.values()) {
            ((DatabaseImpl) database).clear();
        }
        return this;
    }

    public File getDir() {
        return dir;
    }

    public Map<String, Database> getDatabaseCollection() {
        return databaseCollection;
    }

    public short getTick() {
        return tick;
    }

    public Timer getTimer() {
        return timer;
    }

    /**
     * Returns the names of the databases.
     *
     * @return a list of database names
     */
    public List<String> getDatabases() {
        String[] databases = dir.list();
        return databases == null ? new ArrayList<>() : Arrays.asList(databases);
    }

    /**
     * Returns a database for the given {@code name}.
     * If the database does not exist, it will be created.
     *
     * @param name the name of the database
     *
     * @return the database for the given {@code name}
     */
    public Database getDatabase(String name) {
        Database database;

        if (databaseCollection.containsKey(name)) {
            return databaseCollection.get(name);
        }

        File file = new File("database/" + name);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdir();
        }

        database = new DatabaseImpl(name, NetworkUtils.newConcurrentHashMap(), file);
        this.databaseCollection.put(name, database);

        return database;
    }

}
