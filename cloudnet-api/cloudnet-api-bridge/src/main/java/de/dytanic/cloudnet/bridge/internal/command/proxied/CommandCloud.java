/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.command.proxied;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.proxylayout.AutoSlot;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.SimpleServerGroup;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 02.06.2017.
 */
public final class CommandCloud extends Command {

    public CommandCloud()
    {
        super("cloud", "cloudnet.command.cloud");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (args.length > 2)
        {
            if (args[0].equalsIgnoreCase("cmds"))
            {
                if (CloudProxy.getInstance().getCachedServers().containsKey(args[1]))
                {
                    StringBuilder builder = new StringBuilder();
                    for (short i = 2; i < args.length; i++)
                    {
                        builder.append(args[i]).append(" ");
                    }

                    CloudAPI.getInstance().sendConsoleMessage(DefaultType.BUKKIT, args[1], builder.substring(0, builder.length() - 1));
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                            "The information was sent to the cloud");
                    return;
                }
            }
            if (args[0].equalsIgnoreCase("cmdp"))
            {
                StringBuilder builder = new StringBuilder();
                for (short i = 2; i < args.length; i++)
                {
                    builder.append(args[i]).append(" ");
                }

                CloudAPI.getInstance().sendConsoleMessage(DefaultType.BUNGEE_CORD, args[1], builder.substring(0, builder.length() - 1));
                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The information was sent to the cloud");
                return;
            }
        }

        switch (args.length)
        {
            case 1:
                if (args[0].equalsIgnoreCase("help"))
                {
                    commandSender.sendMessage(" ");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "All command arguments");
                    commandSender.sendMessage("§7/cloud toggle autoslot");
                    commandSender.sendMessage("§7/cloud toggle maintenance");
                    commandSender.sendMessage("§7/cloud toggle maintenance <time>");
                    commandSender.sendMessage("§7/cloud setMaxPlayers <maxonlinecount>");
                    commandSender.sendMessage("§7/cloud whitelist <add : remove> <name>");
                    commandSender.sendMessage("§7/cloud start <group> <count>");
                    commandSender.sendMessage("§7/cloud start <group> <template>");
                    commandSender.sendMessage("§7/cloud startcs <name> <memory> <priorityStop>");
                    commandSender.sendMessage("§7/cloud cmds (command server) <server> <command>");
                    commandSender.sendMessage("§7/cloud cmdp (command proxy) <proxy> <command>");
                    commandSender.sendMessage("§7/cloud stop <serverId>");
                    commandSender.sendMessage("§7/cloud stopGroup <group>");
                    commandSender.sendMessage("§7/cloud listProxys");
                    commandSender.sendMessage("§7/cloud listOnline");
                    commandSender.sendMessage("§7/cloud listServers");
                    commandSender.sendMessage("§7/cloud log <server>");
                    commandSender.sendMessage("§7/cloud listGroups");
                    commandSender.sendMessage("§7/cloud rl");
                    commandSender.sendMessage("§7/cloud rlconfig");
                    commandSender.sendMessage("§7/cloud list");
                    commandSender.sendMessage("§7/cloud maintenance <group>");
                    commandSender.sendMessage("§7/cloud copy <server>");
                    commandSender.sendMessage(" ");
                    return;
                }
                if (args[0].equalsIgnoreCase("rl"))
                {
                    CloudAPI.getInstance().sendCloudCommand("reload all");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                            "The information was sent to the cloud");
                    return;
                }
                if (args[0].equalsIgnoreCase("rlconfig"))
                {
                    CloudAPI.getInstance().sendCloudCommand("reload config");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                            "The information was sent to the cloud");
                    return;
                }
                if (args[0].equalsIgnoreCase("list"))
                {
                    commandSender.sendMessage(" ");

                    int maxMemory = 0;
                    int usedMemory = 0;

                    for (WrapperInfo cnsInfo : CloudAPI.getInstance().getWrappers())
                    {
                        commandSender.sendMessage("§8[§7" + cnsInfo.getServerId() + "§8/§7" + cnsInfo.getHostName() + "§8] §7Cores: " + cnsInfo.getAvailableProcessors());
                        maxMemory = maxMemory + cnsInfo.getMemory();
                    }
                    commandSender.sendMessage(" ");
                    for (ProxyInfo simpleProxyInfo : CloudAPI.getInstance().getProxys())
                    {
                        commandSender.sendMessage("§8[§c" + simpleProxyInfo.getServiceId().getServerId() + "§8] §8(§e" + simpleProxyInfo.getOnlineCount() + "§8) : §7" + simpleProxyInfo.getMemory() + "MB");
                        usedMemory = usedMemory + simpleProxyInfo.getMemory();
                    }
                    commandSender.sendMessage(" ");
                    for (ServerInfo simpleProxyInfo : CloudAPI.getInstance().getServers())
                    {
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§8[§c" + simpleProxyInfo.getServiceId().getServerId() + "§8] §8(§e" + simpleProxyInfo.getOnlineCount() + "§8) §e" + simpleProxyInfo.getServerState().name() + " §8: §7" + simpleProxyInfo.getMemory() + "MB"));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + simpleProxyInfo.getServiceId().getServerId()));
                        commandSender.sendMessage(textComponent);
                        usedMemory = usedMemory + simpleProxyInfo.getMemory();
                    }
                    commandSender.sendMessage(" ");

                    commandSender.sendMessage("§7Memory in use: " + usedMemory + "§8/§7" + maxMemory + "MB");
                }
                if (args[0].equalsIgnoreCase("rlperm"))
                {
                    CloudAPI.getInstance().sendCloudCommand("reload config");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                            "The information was sent to the cloud");
                    return;
                }
                if (args[0].equalsIgnoreCase("listProxys"))
                {
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "Proxys:");
                    for (ProxyInfo proxy : CloudAPI.getInstance().getProxys())
                    {
                        commandSender.sendMessage("§7- " + (proxy.isOnline() ? "§e" : "§c") + proxy.getServiceId().getServerId() + " §8(§e" + proxy.getOnlineCount() + "§8) ");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("listServers"))
                {
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "Server:");
                    for (ServerInfo server : CloudProxy.getInstance().getCachedServers().values())
                    {
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§7- " + (server.isOnline() ? "§e" : "§c") + server.getServiceId().getServerId() + "§8(" + server.getOnlineCount() + "§8) §7State: " + server.getServerState()));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + server.getServiceId().getServerId()));
                        commandSender.sendMessage(textComponent);
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("listOnline"))
                {
                    for (CloudPlayer playerWhereAmI : CloudAPI.getInstance().getOnlinePlayers())
                    {
                        commandSender.sendMessage("§7- §e" + playerWhereAmI.getName() + " §7on §e" + playerWhereAmI.getServer() + "/" + playerWhereAmI.getProxy());
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("listGroups"))
                {
                    StringBuilder builder = new StringBuilder();

                    for (SimpleServerGroup group : CloudAPI.getInstance().getCloudNetwork().getServerGroups().values())
                    {
                        builder.append((!group.isMaintenance() ? "§e" : "§c")).append(group.getName()).append("§7, ");
                    }

                    commandSender.sendMessage("§7The following server groups are registered:");
                    commandSender.sendMessage(builder.substring(0));
                    return;
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("toggle"))
                {
                    switch (args[1].toLowerCase())
                    {
                        case "autoslot":
                        {
                            ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                            proxyGroup.getProxyConfig().setAutoSlot(new AutoSlot(proxyGroup.getProxyConfig().getAutoSlot().getDynamicSlotSize(),
                                    !proxyGroup.getProxyConfig().getAutoSlot().isEnabled()));
                            CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The autoslot state was updated.");
                        }
                        return;
                        case "maintenance":
                        {
                            ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                            proxyGroup.getProxyConfig().setMaintenance(!proxyGroup.getProxyConfig().isMaintenance());
                            CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The maintenance state was updated.");
                            return;
                        }
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("log"))
                {
                    if (CloudProxy.getInstance().getCachedServers().containsKey(args[1]) ||
                            CollectionWrapper.filter(CloudAPI.getInstance().getProxys(), new Acceptable<ProxyInfo>() {
                                @Override
                                public boolean isAccepted(ProxyInfo proxyInfo)
                                {
                                    return proxyInfo.getServiceId().getServerId().equalsIgnoreCase(args[1]);
                                }
                            }) != null)
                    {
                        String url = CloudAPI.getInstance().createServerLogUrl(args[1]);
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§n§l§b" + url));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                        commandSender.sendMessage(new TextComponent(TextComponent.fromLegacyText(CloudAPI.getInstance().getPrefix() + "You can review the log at: ")), textComponent);
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The log is dynamic and will be deleted on 10 minutes");
                    } else
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The server doesn't exist.");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("setMaxPlayers"))
                {
                    if (checkAsNumber(args[1]))
                    {
                        ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                        proxyGroup.getProxyConfig().setMaxPlayers(Integer.parseInt(args[1]));
                        CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The maximum onlinecount was updated.");
                    } else
                    {
                        commandSender.sendMessage("§7The second argument is not a number.");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("start"))
                {
                    if (CloudAPI.getInstance().getCloudNetwork().getServerGroups().containsKey(args[1]))
                    {
                        CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(args[1]), new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()), true);
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                "The information was sent to the cloud");
                    } else if (CloudAPI.getInstance().getCloudNetwork().getProxyGroups().containsKey(args[1]))
                    {
                        CloudAPI.getInstance().startProxy(CloudAPI.getInstance().getProxyGroupData(args[1]));
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                "The information was sent to the cloud");
                    } else
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The group doesn't exist.");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("maintenance"))
                {
                    if (CloudAPI.getInstance().getServerGroupMap().containsKey(args[1]))
                    {
                        ServerGroup serverGroup = CloudAPI.getInstance().getServerGroup(args[1]);
                        serverGroup.setMaintenance(!serverGroup.isMaintenance());
                        CloudAPI.getInstance().updateServerGroup(serverGroup);
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                "The information was sent to the cloud");
                    } else
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The group doesn't exist.");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("stop"))
                {
                    if (CloudProxy.getInstance().getCachedServers().containsKey(args[1]))
                    {
                        CloudAPI.getInstance().stopServer(args[1]);
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                "The information was sent to the cloud");
                    } else if (CollectionWrapper.filter(CloudAPI.getInstance().getProxys(), new Acceptable<ProxyInfo>() {
                        @Override
                        public boolean isAccepted(ProxyInfo proxyInfo)
                        {
                            return proxyInfo.getServiceId().getServerId().equalsIgnoreCase(args[1]);
                        }
                    }) != null)
                    {
                        CloudAPI.getInstance().stopProxy(args[1]);
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                "The information was sent to the cloud");
                    } else
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The specified server isn't online.");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("stopGroup"))
                {
                    if(CloudAPI.getInstance().getServerGroupMap().containsKey(args[1]))
                    {
                        List<String> servers = CloudProxy.getInstance().getServers(args[1]);

                        for (String server : servers)
                            CloudAPI.getInstance().stopServer(server);

                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                "The information was sent to the cloud");
                        return;
                    }

                    if(CloudAPI.getInstance().getProxyGroupMap().containsKey(args[1]))
                    {
                        Collection<ProxyInfo> servers = CloudAPI.getInstance().getProxys();

                        for(ProxyInfo proxyInfo : servers)
                            if(proxyInfo.getServiceId().getGroup().equalsIgnoreCase(args[1]))
                                CloudAPI.getInstance().stopProxy(proxyInfo.getServiceId().getServerId());

                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                "The information was sent to the cloud");
                        return;
                    }

                    return;
                }
                if (args[0].equalsIgnoreCase("copy"))
                {
                    CloudAPI.getInstance().sendCloudCommand("copy " + args[1]);
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                            "The information was sent to the cloud");
                    return;
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("toggle"))
                {
                    switch (args[1].toLowerCase())
                    {
                        case "maintenance":
                        {
                            if (!NetworkUtils.checkIsNumber(args[2])) return;
                            ProxyServer.getInstance().getScheduler().schedule(CloudProxy.getInstance().getPlugin(), new Runnable() {
                                @Override
                                public void run()
                                {
                                    ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                                    proxyGroup.getProxyConfig().setMaintenance(!proxyGroup.getProxyConfig().isMaintenance());
                                    CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The maintenance state was updated.");
                                }
                            }, Integer.parseInt(args[2]), TimeUnit.SECONDS);
                            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The maintenance will be changed in " + args[2] + " seconds");
                            return;
                        }
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("start"))
                {
                    if (CloudAPI.getInstance().getServerGroupMap().containsKey(args[1]))
                    {
                        if (checkAsNumber(args[2]))
                        {
                            for (short i = 0; i < Integer.parseInt(args[2]); i++)
                            {
                                CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(args[1]), new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()), true);
                            }
                            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                    "The information was sent to the cloud");
                        } else
                        {
                            ServerGroup serverGroup = CloudAPI.getInstance().getServerGroup(args[1]);
                            if (CollectionWrapper.filter(serverGroup.getTemplates(), new Acceptable<Template>() {
                                @Override
                                public boolean isAccepted(Template value)
                                {
                                    return value.getName().equalsIgnoreCase(args[2]);
                                }
                            }) != null)
                            {
                                CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(args[1]), new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()), true, CollectionWrapper.filter(serverGroup.getTemplates(), new Acceptable<Template>() {
                                    @Override
                                    public boolean isAccepted(Template value)
                                    {
                                        return value.getName().equalsIgnoreCase(args[2]);
                                    }
                                }));
                                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                        "The information was sent to the cloud");
                            }
                        }
                    } else if (CloudAPI.getInstance().getProxyGroupMap().containsKey(args[1]))
                    {
                        if (checkAsNumber(args[2]))
                        {
                            for (short i = 0; i < Integer.parseInt(args[2]); i++)
                            {
                                CloudAPI.getInstance().startProxy(CloudAPI.getInstance().getProxyGroupData(args[1]));
                            }
                            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                    "The information was sent to the cloud");
                        } else
                        {
                            CloudAPI.getInstance().startProxy(CloudAPI.getInstance().getProxyGroupData(args[1]));
                            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                    "The information was sent to the cloud");
                        }
                    } else
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The group doesn't exist.");
                    }
                } else if (args[0].equalsIgnoreCase("whitelist"))
                {
                    if (args[1].equalsIgnoreCase("add"))
                    {
                        ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                        if (proxyGroup.getProxyConfig().getWhitelist().contains(args[2]))
                        {
                            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + " The user " + args[2] + " is already on the whitelist.");
                            return;
                        }
                        proxyGroup.getProxyConfig().getWhitelist().add(args[2]);
                        CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + " You added " + args[2] + " to the whitelist of the maintenance mode.");
                    } else if (args[1].equalsIgnoreCase("remove"))
                    {
                        ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                        proxyGroup.getProxyConfig().getWhitelist().remove(args[2]);
                        CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + " You removed " + args[2] + " from the whitelist of the maintenance mode.");
                    }
                }
                break;
            case 4:
                if (args[0].equalsIgnoreCase("startcs"))
                {
                    if (NetworkUtils.checkIsNumber(args[2]) && Integer.parseInt(args[2]) > 128)
                    {
                        CloudAPI.getInstance().startCloudServer(args[1], Integer.parseInt(args[2]), args[3].equalsIgnoreCase("true"));
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() +
                                "The information was sent to the cloud");
                    } else
                    {
                        commandSender.sendMessage("Invalid arguments!");
                    }
                }
                break;
            default:
                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "Use /cloud help");
                break;
        }

    }

    private boolean checkAsNumber(String input)
    {
        try
        {
            Short.parseShort(input);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

}