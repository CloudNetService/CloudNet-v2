/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.tools;

import de.dytanic.cloudnet.tools.listener.SimpleNameTagsListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Tareko on 26.08.2017.
 */
public class SimpleNameTagsPlugin extends JavaPlugin {

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SimpleNameTagsListener(), this);
    }
}
