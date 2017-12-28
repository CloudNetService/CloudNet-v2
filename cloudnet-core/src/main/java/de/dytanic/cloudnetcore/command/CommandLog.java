/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

/**
 * Created by Tareko on 04.10.2017.
 */
public class CommandLog extends Command {

    public CommandLog()
    {
        super("log", "cloudnet.command.log");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
            {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
                if(minecraftServer != null)
                {
                    String rndm = NetworkUtils.randomString(10);
                    CloudNet.getInstance().getServerLogManager().append(rndm, minecraftServer.getServerId());
                    String x = new StringBuilder(CloudNet.getInstance().getOptionSet().has("ssl") ? "https://" : "http://").append(CloudNet.getInstance().getConfig().getWebServerConfig().getAddress()).append(":").append(CloudNet.getInstance().getConfig().getWebServerConfig().getPort()).append("/cloudnet/log?server=").append(rndm).substring(0);
                    sender.sendMessage("You can see the log at: " + x);
                    sender.sendMessage("The log is dynamic and will delete on 10 minutes");
                }
                else
                {
                    sender.sendMessage("The server doesn't exists!");
                }
            }
                break;
            default:
                sender.sendMessage("log <server> | Creates a web server log");
                break;
        }
    }
}