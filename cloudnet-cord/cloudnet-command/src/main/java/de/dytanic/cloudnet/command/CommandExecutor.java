/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

/**
 * Created by Tareko on 23.05.2017.
 */
public interface CommandExecutor
{
    void onExecuteCommand(CommandSender sender, String[] args);
}
