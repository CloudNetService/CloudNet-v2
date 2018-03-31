/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.command.proxied;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.advanced.DevService;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by Tareko on 14.10.2017.
 */
public final class CommandCloudDev extends Command implements TabExecutor {

    public CommandCloudDev()
    {
        super("cdev", "cloudnet.command.cdev");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        switch (args.length)
        {
            case 0:
                for (ServerInfo serverInfo : CloudProxy.getInstance().getCachedServers().values())
                {
                    if (serverInfo.getServerConfig().getProperties().contains(NetworkUtils.DEV_PROPERTY))
                    {
                        DevService service = serverInfo.getServerConfig().getProperties().getObject(NetworkUtils.DEV_PROPERTY, DevService.class);
                        if (service.getUniqueId().equals(((ProxiedPlayer) commandSender).getUniqueId()))
                        {
                            ((ProxiedPlayer) commandSender).connect(ProxyServer.getInstance().getServerInfo(serverInfo.getServiceId().getServerId()));
                            return;
                        }
                    }
                }
                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cdev forTemplate <group> <template>");
                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cdev forTemplate <group>");
                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cdev testServer <group>");
                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cdev testServer <group> <template>");
                break;
            case 2:
            {
                for (ServerInfo serverInfo : CloudProxy.getInstance().getCachedServers().values())
                {
                    if (serverInfo.getServerConfig().getProperties().contains(NetworkUtils.DEV_PROPERTY))
                    {
                        DevService service = serverInfo.getServerConfig().getProperties().getObject(NetworkUtils.DEV_PROPERTY, DevService.class);
                        if (service.getUniqueId().equals(((ProxiedPlayer) commandSender).getUniqueId()))
                        {
                            ((ProxiedPlayer) commandSender).connect(ProxyServer.getInstance().getServerInfo(serverInfo.getServiceId().getServerId()));
                            return;
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("forTemplate"))
                {
                    if (!CloudAPI.getInstance().getServerGroupMap().containsKey(args[1]))
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The group doesn't exist");
                        return;
                    }
                    ServerConfig serverConfig = new ServerConfig(true, commandSender.getName(), new Document(NetworkUtils.DEV_PROPERTY, new DevService(((
                            ProxiedPlayer
                            ) commandSender).getUniqueId(), true)), System.currentTimeMillis());
                    CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(args[1]), serverConfig, "Dev" + args[1] + "-" + commandSender.getName());
                    ((ProxiedPlayer) commandSender).sendMessage(CloudAPI.getInstance().getPrefix() + "The server will start up now...");
                    return;
                }
                if (args[0].equalsIgnoreCase("testServer"))
                {
                    if (!CloudAPI.getInstance().getServerGroupMap().containsKey(args[1]))
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The group doesn't exist");
                        return;
                    }
                    ServerConfig serverConfig = new ServerConfig(true, commandSender.getName(), new Document(), System.currentTimeMillis());
                    CloudAPI.getInstance().startCloudServer("TestServer-" + ((ProxiedPlayer) commandSender).getUniqueId(), serverConfig, 356, true);
                    ((ProxiedPlayer) commandSender).sendMessage(CloudAPI.getInstance().getPrefix() + "The server will start up now...");
                    return;
                }
            }
            case 3:
            {
                for (ServerInfo serverInfo : CloudProxy.getInstance().getCachedServers().values())
                {
                    if (serverInfo.getServerConfig().getProperties().contains(NetworkUtils.DEV_PROPERTY))
                    {
                        DevService service = serverInfo.getServerConfig().getProperties().getObject(NetworkUtils.DEV_PROPERTY, DevService.class);
                        if (service.getUniqueId().equals(((ProxiedPlayer) commandSender).getUniqueId()))
                        {
                            ((ProxiedPlayer) commandSender).connect(ProxyServer.getInstance().getServerInfo(serverInfo.getServiceId().getServerId()));
                            return;
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("forTemplate"))
                {
                    if (!CloudAPI.getInstance().getServerGroupMap().containsKey(args[1]))
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The group doesn't exist");
                        return;
                    }

                    ServerGroup serverGroup = CloudAPI.getInstance().getServerGroup(args[1]);
                    if (CollectionWrapper.filter(serverGroup.getTemplates(), new Acceptable<Template>() {
                        @Override
                        public boolean isAccepted(Template template)
                        {
                            return template.getName().equalsIgnoreCase(args[2]);
                        }
                    }) == null)
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The template doesn't exist");
                        return;
                    }

                    ServerConfig serverConfig = new ServerConfig(true, commandSender.getName(), new Document(NetworkUtils.DEV_PROPERTY, new DevService(((
                            ProxiedPlayer
                            ) commandSender).getUniqueId(), true)), System.currentTimeMillis());
                    CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(args[1]), serverConfig, CloudAPI.getInstance().getServerGroupData(args[1]).getMemory(),
                            true, new Properties(), CollectionWrapper.filter(serverGroup.getTemplates(), new Acceptable<Template>() {
                                @Override
                                public boolean isAccepted(Template e)
                                {
                                    return e.getName().equalsIgnoreCase(args[2]);
                                }
                            }), "Dev" + args[1] + "-" + commandSender.getName());
                    ((ProxiedPlayer) commandSender).sendMessage(CloudAPI.getInstance().getPrefix() + "The server will start up now...");
                    return;
                }
                if (args[0].equalsIgnoreCase("testServer"))
                {
                    if (!CloudAPI.getInstance().getServerGroupMap().containsKey(args[1]))
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The group doesn't exist");
                        return;
                    }

                    ServerGroup serverGroup = CloudAPI.getInstance().getServerGroup(args[1]);
                    if (CollectionWrapper.filter(serverGroup.getTemplates(), new Acceptable<Template>() {
                        @Override
                        public boolean isAccepted(Template template)
                        {
                            return template.getName().equalsIgnoreCase(args[2]);
                        }
                    }) == null)
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The template doesn't exist");
                        return;
                    }
                    ServerConfig serverConfig = new ServerConfig(true, commandSender.getName(), new Document(), System.currentTimeMillis());
                    /*
                    CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(args[1]), serverConfig,
                            CloudAPI.getInstance().getServerGroupData(args[1]).getMemory(),
                            new String[0], null, "TestServer-" + commandSender.getName(), false, true, new Properties(),
                            null, Arrays.asList()
                            , "Dev" + args[1] + "-" + commandSender.getName());
                    */
                    CloudAPI.getInstance().startCloudServer("TestServer-" + ((ProxiedPlayer) commandSender).getUniqueId(), serverConfig, 356, true);
                    ((ProxiedPlayer) commandSender).sendMessage(CloudAPI.getInstance().getPrefix() + "The server will start up now...");
                    return;
                }
            }
            break;
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings)
    {
        return strings.length == 1 ? Arrays.asList("autocomplete with 1 args length") : Arrays.asList("autoComplete with another args length");
    }
}