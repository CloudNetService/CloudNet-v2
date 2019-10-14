/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.tools;

import de.dytanic.cloudnet.tools.listener.ChatListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Tareko on 26.08.2017.
 */
public final class ChatPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }
}
