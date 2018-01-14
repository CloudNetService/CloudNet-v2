/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

/**
 * Created by Tareko on 26.07.2017.
 */
public class CommandHelp extends Command {

    public CommandHelp()
    {
        super("help", "cloudnet.command.help");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {

        StringBuilder proxyGrouPBuilder = new StringBuilder();
        for(String group : CloudNet.getInstance().getProxyGroups().keySet())
        {
            proxyGrouPBuilder.append(group).append(", ");
        }

        StringBuilder serverGrouPBuilder = new StringBuilder();
        for(String group : CloudNet.getInstance().getServerGroups().keySet())
        {
            serverGrouPBuilder.append(group).append(", ");
        }

        sender.sendMessage(
                "",
                "create | Creates new Wrapper, ServerGroup, PermissionGroup, ProxyGroup or custom server",
                "stop | Stops CloudNet and all Wrappers",
                "clear | Clears the console",
                "reload | Reloads the config and modules",
                "shutdown | Stops all wrappers, proxys, servers or proxy/server groups",
                "perms | Manages the permissions of the permissions-system",
                "screen | Shows you the console of one server",
                "cmd | Executes a command on a game server or proxy server",
                "statistic | Shows a list of all statistics of cloudnet!",
                "modules | Lists all modules, versions and authors",
                "clearcache | Clears the plugin and template cache for all wrappers",
                "list | Lists some information of the network",
                "install | Installs a module or a url for a public template",
                "installplugin | Installs plugin for one server",
                "copy | Copies a minecraft server to a template which is loaded local",
                "delete | Deletes a servergroup or custom server",
                "log | Creates a web server log",
                "Server groups:",
                serverGrouPBuilder.substring(0),
                "Proxy groups: ",
                proxyGrouPBuilder.substring(0),
                "The Cloud uses " + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L) + "/" + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L) + "MB",
                "CPU on this instance " + new DecimalFormat("##.##").format(NetworkUtils.internalCpuUsage()) + "/100 %",
                " "
        );
    }
}