/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;

import java.io.IOException;

public final class CommandClear extends Command {

    public CommandClear()
    {
        super("clear", "cloudnet.command.clear");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        try
        {
            CloudNet.getInstance().getLogger().getReader().clearScreen();
        } catch (IOException e)
        {
        }
    }
}