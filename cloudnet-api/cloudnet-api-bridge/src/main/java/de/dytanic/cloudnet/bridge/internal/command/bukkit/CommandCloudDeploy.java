/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.command.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Tareko on 14.10.2017.
 */
public class CommandCloudDeploy extends Command {

    public CommandCloudDeploy()
    {
        super("cdeploy");
        setPermission("cloudnet.command.cdeploy");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args)
    {
        if(!testPermission(commandSender)) return false;

        switch (args.length)
        {
            case 1:
            {
                CloudAPI.getInstance().sendCloudCommand("copy " + CloudAPI.getInstance().getServerId() + " " + args[0]);
                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "Server will be deployed to " + args[0]);
            }
                break;
            default:
                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cdeploy <template>");
                break;
        }

        return true;
    }
}