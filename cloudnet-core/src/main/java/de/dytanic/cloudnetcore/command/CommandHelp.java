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
                "create | creates new Wrapper, ServerGroups, PermissionGroups, ProxyGroups, custom server",
                "stop | Stop the CloudNet and all Wrapper's",
                "clear | Clear the console",
                "reload | Reload the config and modules",
                "shutdown | Stop wrappers, proxys, servers or proxy/server groups",
                "perms | Manage the permissions from the permissions-system",
                "screen | managed the console of one server",
                "cmd | Executes a command on one game server or proxy server",
                "statistic | A list of all statistic of cloudnet!",
                "modules | Lists all modules, versions and authors",
                "clearcache | Clear for all wrappers the plugin and template cache",
                "list | Lists some informations of the network",
                "install | Install a module or a url for a public template",
                "installplugin | Plugin install for one server",
                "copy | Copied a minecraft server to the template which is loaded local",
                "delete | Delete a servergroup or custom server",
                "log | Creates a web server log",
                "Server groups:",
                serverGrouPBuilder.substring(0),
                "Proxy groups: ",
                proxyGrouPBuilder.substring(0),
                "The Cloud use " + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L) + "/" + (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L) + "MB",
                "CPU on this instance " + new DecimalFormat("##.##").format(NetworkUtils.internalCpuUsage()) + "/100 %",
                " "
        );
    }
}