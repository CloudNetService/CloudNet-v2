/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.database;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.database.Database;
import lombok.Getter;

import java.io.File;
import java.util.*;


/**
 * Created by Tareko on 01.07.2017.
 */
@Getter
public class DatabaseManager
            implements Runnable{

    private final File dir;
    private final Thread thread;
    private short tick = 1;

    private java.util.Map<String, Database> databaseCollection = NetworkUtils.newConcurrentHashMap();

    public DatabaseManager()
    {
        dir = new File("database");
        dir.mkdir();

        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public List<String> getDatabases()
    {
        return Arrays.asList(dir.list());
    }

    public Database getDatabase(String name)
    {
        Database database = null;

        if(databaseCollection.containsKey(name))
        {
            return databaseCollection.get(name);
        }

        File file = new File("database/" + name);
        if(!file.exists())
        {
            file.mkdir();
        }

        database = new DatabaseImpl(name, NetworkUtils.newConcurrentHashMap(), file);
        this.databaseCollection.put(name, database);

        return database;
    }

    public DatabaseManager save()
    {
        for(Database database : databaseCollection.values())
        {
            ((DatabaseImpl)database).save();
        }
        return this;
    }

    public DatabaseManager clear()
    {
        for(Database database : databaseCollection.values())
        {
            ((DatabaseImpl)database).clear();
        }
        return this;
    }

    @Override
    public void run()
    {
        while (true)
        {

            save();

            tick++;
            if(tick == 6)
            {
                clear();
                tick = 0;
            }

            try
            {
                Thread.sleep(60000);
            } catch (InterruptedException e) {}
        }
    }
}