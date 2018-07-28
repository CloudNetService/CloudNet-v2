/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

/**
 * Calls
 */
public class CommandDelete extends Command {

    public CommandDelete()
    {
        super("delete", "cloudnet.command.delete");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 2:
                if (args[0].equalsIgnoreCase("serverGroup"))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1]))
                    {
                        ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[1]);
                        CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
                        CloudNet.getInstance().getServerGroups().remove(args[1]);
                        CloudNet.getInstance().getNetworkManager().updateAll();
                        for (MinecraftServer minecraftServer : CloudNet.getInstance().getServers(args[1]))
                        {
                            minecraftServer.getWrapper().stopServer(minecraftServer);
                        }
                        sender.sendMessage("The group was successfully deleted");
                    } else
                    {
                        sender.sendMessage("The server group doesn't exists");
                    }
                }
                break;
            default:
                sender.sendMessage(
                        "delete serverGroup <name>"
                );
                break;
        }
    }
}