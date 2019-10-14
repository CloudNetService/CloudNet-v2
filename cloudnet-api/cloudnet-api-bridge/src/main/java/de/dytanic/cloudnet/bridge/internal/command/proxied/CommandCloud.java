/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.command.proxied;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.database.Database;
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
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 02.06.2017.
 */
public final class CommandCloud extends Command implements TabExecutor {

    public CommandCloud() {
        super("cloud", "cloudnet.command.cloud");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        CloudAPI.getInstance().getLogger().finest(String.format("%s executed %s with arguments %s",
                                                                commandSender,
                                                                this,
                                                                Arrays.toString(args)));
        if (args.length > 2) {
            if (args[0].equalsIgnoreCase("cmds") && commandSender.hasPermission("cloudnet.command.cloud.commandserver")) {
                if (CloudProxy.getInstance().getCachedServers().containsKey(args[1])) {
                    StringBuilder builder = new StringBuilder();

                    for (short i = 2; i < args.length; i++) {
                        builder.append(args[i]).append(NetworkUtils.SPACE_STRING);
                    }

                    CloudAPI.getInstance().sendConsoleMessage(DefaultType.BUKKIT, args[1], builder.substring(0, builder.length() - 1));
                    commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                   .getPrefix() + "The information was sent to the cloud"));
                    return;
                }
            }
            if (args[0].equalsIgnoreCase("cmdp") && commandSender.hasPermission("cloudnet.command.cloud.commandproxy")) {
                StringBuilder builder = new StringBuilder();

                for (short i = 2; i < args.length; i++) {
                    builder.append(args[i]).append(NetworkUtils.SPACE_STRING);
                }

                CloudAPI.getInstance().sendConsoleMessage(DefaultType.BUNGEE_CORD, args[1], builder.substring(0, builder.length() - 1));
                commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                               .getPrefix() + "The information was sent to the cloud"));
                return;
            }
        }

        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("whitelist")) {
                    commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                   .getPrefix() + "Whitelisted players from " + CloudProxy.getInstance()
                                                                                                                                          .getProxyGroup()
                                                                                                                                          .getName()));
                    for (String entry : CloudProxy.getInstance().getProxyGroup().getProxyConfig().getWhitelist()) {
                        commandSender.sendMessage(TextComponent.fromLegacyText("§7- " + entry));
                    }
                } else if (args[0].equalsIgnoreCase("rl") && commandSender.hasPermission("cloudnet.command.cloud.reload")) {
                    CloudAPI.getInstance().sendCloudCommand("reload config");
                    commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                   .getPrefix() + "The information was sent to the cloud"));
                    return;
                } else if (args[0].equalsIgnoreCase("statistics") && commandSender.hasPermission("cloudnet.command.cloud.statistics")) {
                    Document document = CloudAPI.getInstance().getStatistics();

                    commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance().getPrefix() + "Statistics"));

                    for (String key : document.keys()) {
                        if (!Database.UNIQUE_NAME_KEY.equalsIgnoreCase(key)) {
                            commandSender.sendMessage(TextComponent.fromLegacyText("§3" + key + "§8| §e " + document.get(key).toString()));
                        }
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("version") && commandSender.hasPermission("cloudnet.command.cloud.version")) {
                    commandSender.sendMessage(TextComponent.fromLegacyText("CloudNet " + NetworkUtils.class.getPackage()
                                                                                                           .getSpecificationVersion() + " #" + NetworkUtils.class
                        .getPackage()
                        .getImplementationVersion() + " by Dytanic"));
                    return;
                } else if (args[0].equalsIgnoreCase("list") && commandSender.hasPermission("cloudnet.command.cloud.list")) {
                    commandSender.sendMessage(TextComponent.fromLegacyText(NetworkUtils.SPACE_STRING));

                    int maxMemory = 0;
                    int usedMemory = 0;

                    Map<String, Collection<ServerInfo>> groupSorted = new LinkedHashMap<>();

                    for (WrapperInfo cnsInfo : CloudAPI.getInstance().getWrappers()) {
                        commandSender.sendMessage(TextComponent.fromLegacyText("§8[§7" + cnsInfo.getServerId() + "§8/§7" + cnsInfo.getHostName() + "§8] §7Cores: " + cnsInfo
                            .getAvailableProcessors()));
                        maxMemory = maxMemory + cnsInfo.getMemory();
                    }
                    commandSender.sendMessage(TextComponent.fromLegacyText(NetworkUtils.SPACE_STRING));
                    for (ProxyInfo simpleProxyInfo : CloudAPI.getInstance().getProxys()) {
                        commandSender.sendMessage(TextComponent.fromLegacyText("§8[§c" + simpleProxyInfo.getServiceId()
                                                                                                        .getServerId() + "§8] §8(§e" + simpleProxyInfo
                            .getOnlineCount() + "§8) : §7" + simpleProxyInfo.getMemory() + "MB"));
                        usedMemory = usedMemory + simpleProxyInfo.getMemory();
                    }

                    commandSender.sendMessage(TextComponent.fromLegacyText(NetworkUtils.SPACE_STRING));
                    for (ServerInfo simpleProxyInfo : CloudAPI.getInstance().getServers()) {
                        if (simpleProxyInfo.getServiceId().getGroup() != null) {
                            if (!groupSorted.containsKey(simpleProxyInfo.getServiceId().getGroup())) {
                                groupSorted.put(simpleProxyInfo.getServiceId().getGroup(), new ArrayList<>());
                            }

                            groupSorted.get(simpleProxyInfo.getServiceId().getGroup()).add(simpleProxyInfo);
                            continue;
                        }
                        sendServerInfo(commandSender, simpleProxyInfo);
                        usedMemory = usedMemory + simpleProxyInfo.getMemory();
                    }

                    commandSender.sendMessage(TextComponent.fromLegacyText(NetworkUtils.SPACE_STRING));

                    for (Map.Entry<String, Collection<ServerInfo>> entry : groupSorted.entrySet()) {
                        commandSender.sendMessage(TextComponent.fromLegacyText("§7Group: §e" + entry.getKey()));

                        for (ServerInfo serverInfo : entry.getValue()) {
                            sendServerInfo(commandSender, serverInfo);
                            usedMemory = usedMemory + serverInfo.getMemory();
                        }

                        commandSender.sendMessage(TextComponent.fromLegacyText(NetworkUtils.SPACE_STRING));
                    }

                    commandSender.sendMessage(TextComponent.fromLegacyText("§7Memory in use: " + usedMemory + "§8/§7" + maxMemory + "MB"));
                } else if (args[0].equalsIgnoreCase("listProxys") && commandSender.hasPermission("cloudnet.command.listproxys")) {
                    commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance().getPrefix() + "Proxys:"));
                    for (ProxyInfo proxy : CloudAPI.getInstance().getProxys()) {
                        commandSender.sendMessage(TextComponent.fromLegacyText("§7- " + (proxy.isOnline() ? "§e" : "§c") + proxy.getServiceId()
                                                                                                                                .getServerId() + " §8(§e" + proxy
                            .getOnlineCount() + "§8) "));
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("listServers") && commandSender.hasPermission("cloudnet.command.cloud.listservers")) {
                    commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance().getPrefix() + "Server:"));
                    for (ServerInfo server : CloudProxy.getInstance().getCachedServers().values()) {
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§7- " + (server.isOnline() ? "§e" : "§c") + server
                            .getServiceId()
                            .getServerId() + "§8(" + server.getOnlineCount() + "§8) §7State: " + server.getServerState()));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                   "/server " + server.getServiceId().getServerId()));
                        commandSender.sendMessage(textComponent);
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("listOnline") && commandSender.hasPermission("cloudnet.command.cloud.listonline")) {
                    for (CloudPlayer playerWhereAmI : CloudAPI.getInstance().getOnlinePlayers()) {
                        commandSender.sendMessage(TextComponent.fromLegacyText("§7- §e" + playerWhereAmI.getName() + " §7on §e" + playerWhereAmI
                            .getServer() + NetworkUtils.SLASH_STRING + playerWhereAmI.getProxy()));
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("listGroups") && commandSender.hasPermission("cloudnet.command.cloud.listgroups")) {
                    StringBuilder builder = new StringBuilder();

                    for (SimpleServerGroup group : CloudAPI.getInstance().getCloudNetwork().getServerGroups().values()) {
                        builder.append((group.isMaintenance() ? "§c" : "§e")).append(group.getName()).append("§7, ");
                    }

                    commandSender.sendMessage(TextComponent.fromLegacyText("§7The following server groups are registered:"));
                    commandSender.sendMessage(TextComponent.fromLegacyText(builder.toString()));
                    return;
                } else if (args[0].equalsIgnoreCase("debug") && commandSender.hasPermission("cloudnet.command.cloud.debug")) {
                    CloudAPI.getInstance().setDebug(!CloudAPI.getInstance().isDebug());
                    if (CloudAPI.getInstance().isDebug()) {
                        commandSender.sendMessage(TextComponent.fromLegacyText("§aDebug output for proxy has been enabled."));
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacyText("§cDebug output for proxy has been disabled."));
                    }
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("toggle")) {
                    switch (args[1].toLowerCase()) {
                        case "autoslot": {
                            if (commandSender.hasPermission("cloudnet.command.cloud.autoslot")) {
                                ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                                proxyGroup.getProxyConfig().setAutoSlot(new AutoSlot(proxyGroup.getProxyConfig()
                                                                                               .getAutoSlot()
                                                                                               .getDynamicSlotSize(),
                                                                                     !proxyGroup.getProxyConfig()
                                                                                                .getAutoSlot()
                                                                                                .isEnabled()));
                                CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                                commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                               .getPrefix() + "The autoslot state was updated."));
                            }
                        }
                        return;
                        case "maintenance": {
                            if (commandSender.hasPermission("cloudnet.command.cloud.maintenance")) {
                                ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                                proxyGroup.getProxyConfig().setMaintenance(!proxyGroup.getProxyConfig().isMaintenance());
                                CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                                commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                               .getPrefix() + "The maintenance state was updated."));
                            }
                            return;
                        }
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("log") && commandSender.hasPermission("cloudnet.command.cloud.log")) {
                    if (CloudProxy.getInstance().getCachedServers().containsKey(args[1]) || CloudAPI.getInstance()
                                                                                                    .getProxys()
                                                                                                    .stream()
                                                                                                    .anyMatch(proxyInfo -> proxyInfo.getServiceId()
                                                                                                                                    .getServerId()
                                                                                                                                    .equalsIgnoreCase(
                                                                                                                                        args[1]))) {
                        String url = CloudAPI.getInstance().createServerLogUrl(args[1]);
                        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§n§l§b" + url));
                        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                        commandSender.sendMessage(new TextComponent(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                                         .getPrefix() + "You can review the log at: ")),
                                                  textComponent);
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The log is dynamic and will be deleted on 10 minutes"));
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The server doesn't exist."));
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("setMaxPlayers") && commandSender.hasPermission("cloudnet.command.cloud.setmaxplayers")) {
                    if (isNumber(args[1])) {
                        ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                        proxyGroup.getProxyConfig().setMaxPlayers(Integer.parseInt(args[1]));
                        CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The maximum onlinecount was updated."));
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacyText("§7The second argument is not a number."));
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("start") && commandSender.hasPermission("cloudnet.command.cloud.start")) {
                    if (CloudAPI.getInstance().getCloudNetwork().getServerGroups().containsKey(args[1])) {
                        CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(args[1]),
                                                               new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()),
                                                               true);
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                    } else if (CloudAPI.getInstance().getCloudNetwork().getProxyGroups().containsKey(args[1])) {
                        CloudAPI.getInstance().startProxy(CloudAPI.getInstance().getProxyGroupData(args[1]));
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The group doesn't exist."));
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("maintenance") && commandSender.hasPermission("cloudnet.command.cloud.maintenancegroup")) {
                    if (CloudAPI.getInstance().getServerGroupMap().containsKey(args[1])) {
                        ServerGroup serverGroup = CloudAPI.getInstance().getServerGroup(args[1]);
                        serverGroup.setMaintenance(!serverGroup.isMaintenance());
                        CloudAPI.getInstance().updateServerGroup(serverGroup);
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The group doesn't exist."));
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("stop") && commandSender.hasPermission("cloudnet.command.cloud.stop")) {
                    if (CloudProxy.getInstance().getCachedServers().containsKey(args[1])) {
                        CloudAPI.getInstance().stopServer(args[1]);
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                    } else if (CloudAPI.getInstance().getProxys().stream().anyMatch(proxyInfo -> proxyInfo.getServiceId()
                                                                                                          .getServerId()
                                                                                                          .equalsIgnoreCase(args[1]))) {
                        CloudAPI.getInstance().stopProxy(args[1]);
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The specified server isn't online."));
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("stopGroup") && commandSender.hasPermission("cloudnet.command.cloud.stopgroup")) {
                    if (CloudAPI.getInstance().getServerGroupMap().containsKey(args[1])) {
                        List<String> servers = CloudProxy.getInstance().getServers(args[1]);

                        for (String server : servers) {
                            CloudAPI.getInstance().stopServer(server);
                        }

                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                        return;
                    }

                    if (CloudAPI.getInstance().getProxyGroupMap().containsKey(args[1])) {
                        Collection<ProxyInfo> servers = CloudAPI.getInstance().getProxys();

                        for (ProxyInfo proxyInfo : servers) {
                            if (proxyInfo.getServiceId().getGroup().equalsIgnoreCase(args[1])) {
                                CloudAPI.getInstance().stopProxy(proxyInfo.getServiceId().getServerId());
                            }
                        }

                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                        return;
                    }

                    return;
                } else if (args[0].equalsIgnoreCase("ustopGroup") && commandSender.hasPermission("cloudnet.command.cloud.useless-stopgroup")) {
                    if (CloudAPI.getInstance().getServerGroupMap().containsKey(args[1])) {

                        CloudProxy.getInstance()
                                  .getCachedServers()
                                  .values()
                                  .stream()
                                  .filter(serverInfo -> serverInfo.getServiceId()
                                                                  .getGroup() != null && serverInfo.getServiceId()
                                                                                                   .getGroup()
                                                                                                   .equalsIgnoreCase(args[1]))
                                  .filter(serverInfo -> serverInfo.getOnlineCount() == 0)
                                  .forEach(serverInfo -> CloudAPI.getInstance().stopServer(serverInfo.getServiceId().getServerId()));

                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                        return;
                    }
                    if (CloudAPI.getInstance().getProxyGroupMap().containsKey(args[1])) {
                        Collection<ProxyInfo> servers = CloudAPI.getInstance().getProxys();

                        for (ProxyInfo proxyInfo : servers) {
                            if (proxyInfo.getServiceId().getGroup().equalsIgnoreCase(args[1]) && proxyInfo.getOnlineCount() == 0) {
                                CloudAPI.getInstance().stopProxy(proxyInfo.getServiceId().getServerId());
                            }
                        }

                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                        return;
                    }

                    return;
                } else if (args[0].equalsIgnoreCase("copy") && commandSender.hasPermission("cloudnet.command.cloud.copy")) {
                    CloudAPI.getInstance().sendCloudCommand("copy " + args[1]);
                    commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                   .getPrefix() + "The information was sent to the cloud"));
                    return;
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("copy")) {
                    if (!CloudProxy.getInstance().getCachedServers().containsKey(args[1])) {
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The server doesn't exists"));
                        return;
                    }

                    CloudAPI.getInstance().copyDirectory(CloudProxy.getInstance().getCachedServers().get(args[1]), args[2]);
                    commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                   .getPrefix() + "The wrapper tried to copy the directory..."));

                    return;
                }
                if (args[0].equalsIgnoreCase("toggle")) {
                    if (args[1].toLowerCase().equals("maintenance")) {
                        if (commandSender.hasPermission("cloudnet.command.cloud.maintenance")) {
                            if (!NetworkUtils.checkIsNumber(args[2])) {
                                return;
                            }
                            ProxyServer.getInstance().getScheduler().schedule(CloudProxy.getInstance().getPlugin(), () -> {
                                ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                                proxyGroup.getProxyConfig().setMaintenance(!proxyGroup.getProxyConfig().isMaintenance());
                                CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                                commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                               .getPrefix() + "The maintenance state was updated."));
                            }, Integer.parseInt(args[2]), TimeUnit.SECONDS);
                            commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                           .getPrefix() + "The maintenance will be changed in " + args[2] + " seconds"));
                        }
                        return;
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("start") && commandSender.hasPermission("cloudnet.command.cloud.start")) {
                    if (CloudAPI.getInstance().getServerGroupMap().containsKey(args[1])) {
                        if (NetworkUtils.checkIsNumber(args[2])) {
                            for (short i = 0; i < Integer.parseInt(args[2]); i++) {
                                CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(args[1]),
                                                                       new ServerConfig(false,
                                                                                        "extra",
                                                                                        new Document(),
                                                                                        System.currentTimeMillis()),
                                                                       true);
                            }
                            commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                           .getPrefix() + "The information was sent to the cloud"));
                        } else {
                            ServerGroup serverGroup = CloudAPI.getInstance().getServerGroup(args[1]);
                            Optional<Template> template = serverGroup.getTemplates()
                                                                     .stream()
                                                                     .filter(t -> t.getName()
                                                                                   .equalsIgnoreCase(args[2]))
                                                                     .findFirst();
                            template.ifPresent(value -> {
                                CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(args[1]),
                                                                       new ServerConfig(false,
                                                                                        "extra",
                                                                                        new Document(),
                                                                                        System.currentTimeMillis()),
                                                                       true,
                                                                       value);
                                commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                               .getPrefix() + "The information was sent to the cloud"));
                            });
                        }
                    } else if (CloudAPI.getInstance().getProxyGroupMap().containsKey(args[1])) {
                        if (NetworkUtils.checkIsNumber(args[2])) {
                            for (short i = 0; i < Integer.parseInt(args[2]); i++) {
                                CloudAPI.getInstance().startProxy(CloudAPI.getInstance().getProxyGroupData(args[1]));
                            }
                            commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                           .getPrefix() + "The information was sent to the cloud"));
                        } else {
                            CloudAPI.getInstance().startProxy(CloudAPI.getInstance().getProxyGroupData(args[1]));
                            commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                           .getPrefix() + "The information was sent to the cloud"));
                        }
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The group doesn't exist."));
                    }
                } else if (args[0].equalsIgnoreCase("whitelist") && commandSender.hasPermission("cloudnet.command.cloud.whitelist")) {
                    if (args[1].equalsIgnoreCase("add")) {
                        ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                        if (proxyGroup.getProxyConfig().getWhitelist().contains(args[2])) {
                            commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                           .getPrefix() + " The user " + args[2] + " is already on the whitelist."));
                            return;
                        }
                        proxyGroup.getProxyConfig().getWhitelist().add(args[2]);
                        CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + " You added " + args[2] + " to the whitelist of the maintenance mode."));
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();
                        proxyGroup.getProxyConfig().getWhitelist().remove(args[2]);
                        CloudAPI.getInstance().updateProxyGroup(proxyGroup);
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + " You removed " + args[2] + " from the whitelist of the maintenance mode."));
                    }
                }
                break;
            case 4:
                if (args[0].equalsIgnoreCase("startcs") && commandSender.hasPermission("cloudnet.command.cloud.startcs")) {
                    if (NetworkUtils.checkIsNumber(args[2]) && Integer.parseInt(args[2]) > 128) {
                        CloudAPI.getInstance().startCloudServer(args[1], Integer.parseInt(args[2]), args[3].equalsIgnoreCase("true"));
                        commandSender.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance()
                                                                                       .getPrefix() + "The information was sent to the cloud"));
                    } else {
                        commandSender.sendMessage(TextComponent.fromLegacyText("Invalid arguments!"));
                    }
                }
                break;
            default:
            	Lists.newArrayList(NetworkUtils.SPACE_STRING,
                        CloudAPI.getInstance().getPrefix() + "All command arguments",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud toggle autoslot",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud toggle maintenance",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud toggle maintenance <time>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud setMaxPlayers <maxonlinecount>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud whitelist",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud whitelist <add : remove> <name>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud start <group> <count>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud start <group> <template>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud startcs <name> <memory> <priorityStop>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud cmds (command server) <server> <command>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud cmdp (command proxy) <proxy> <command>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud stop <server>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud stopGroup <group>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud ustopGroup <group>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud listProxys",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud listOnline",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud listServers",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud log <server>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud listGroups",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud rl",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud list",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud maintenance <group>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud copy <server>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud copy <server> <directory>",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud version",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud statistics",
                        CloudAPI.getInstance().getPrefix() + "§7/cloud debug",
                        NetworkUtils.SPACE_STRING)
                        .stream()
                        .map(TextComponent::fromLegacyText)
                        .forEach(commandSender::sendMessage);

                break;
        }

    }

    private void sendServerInfo(CommandSender commandSender, ServerInfo serverInfo) {
        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText("§8[§c" + serverInfo.getServiceId()
                                                                                                         .getServerId() + "§8] §8(§e" + serverInfo
            .getOnlineCount() + "§8) §e" + serverInfo.getServerState().name() + " §8: §7" + serverInfo.getMemory() + "MB"));

        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + serverInfo.getServiceId().getServerId()));
        commandSender.sendMessage(textComponent);
    }

    private static boolean isNumber(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {

        switch (args.length) {
            case 1: {
                return ImmutableList.of("toggle",
                                        "setMaxPlayers",
                                        "whitelist",
                                        "start",
                                        "startcs",
                                        "cmds",
                                        "cmdp",
                                        "stop",
                                        "stopGroup",
                                        "ustopGroup",
                                        "listProxys",
                                        "listOnline",
                                        "listServers",
                                        "log",
                                        "listGroups",
                                        "rl",
                                        "list",
                                        "maintenance",
                                        "copy",
                                        "version",
                                        "statistics",
                                        "debug");
            }
            case 2: {
                switch (args[0].toLowerCase(Locale.ENGLISH)) {
                    case "toggle": {
                        return ImmutableList.of("autoslot", "maintenance");
                    }
                    case "whitelist": {
                        return ImmutableList.of("add", "remove");
                    }
                    case "maintenance":
                    case "start":
                    case "stopgroup":
                    case "ustopgroup": {
                        return getProxyAndServerGroups();
                    }
                    case "stop": {
                        return getProxiesAndServers();
                    }
                    case "log": {
                        return ImmutableList.copyOf(CloudProxy.getInstance().getCachedServers().keySet());
                    }
                    case "cmds": {
                        return CloudAPI.getInstance()
                                       .getServers()
                                       .stream()
                                       .map(ServerInfo::getServiceId)
                                       .map(ServiceId::getServerId)
                                       .collect(Collectors.toList());
                    }
                    case "cmdp": {
                        return CloudAPI.getInstance().getProxys().stream().map(ProxyInfo::getServiceId).map(ServiceId::getServerId).collect(
                            Collectors.toList());
                    }

                }
                break;
            }
            case 3: {
                if (args[0].toLowerCase(Locale.ENGLISH).equals("whitelist")) {
                    return CloudAPI.getInstance().getOnlinePlayers().stream().map(CloudPlayer::getName).collect(Collectors.toList());
                }
                break;
            }
            default: {
                return ImmutableList.of();
            }
        }
        return new LinkedList<>();
    }

    private List<String> getProxyAndServerGroups() {
        LinkedList<String> groups = new LinkedList<>(CloudAPI.getInstance().getProxyGroupMap().keySet());
        groups.addAll(CloudAPI.getInstance().getServerGroupMap().keySet());
        groups.sort(Collections.reverseOrder());
        return groups;
    }

    private List<String> getProxiesAndServers() {
        LinkedList<String> groups = CloudAPI.getInstance()
                                            .getProxys()
                                            .stream()
                                            .map(ProxyInfo::getServiceId)
                                            .map(ServiceId::getServerId)
                                            .collect(Collectors.toCollection(LinkedList::new));
        groups.addAll(CloudAPI.getInstance()
                              .getServers()
                              .stream()
                              .map(ServerInfo::getServiceId)
                              .map(ServiceId::getServerId)
                              .collect(Collectors.toList()));
        groups.sort(Collections.reverseOrder());
        return groups;
    }
}
