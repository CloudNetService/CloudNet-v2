/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.stream.Collectors;

public final class CommandDelete extends Command {

    public CommandDelete() {
        super("delete", "cloudnet.command.delete");

        description = "Deletes a servergroup, proxygroup, wrapper or templates of a servergroup";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                if (args[0].equalsIgnoreCase("serverGroup")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1])) {
                        ServerGroup serverGroup = CloudNet.getInstance().getServerGroup(args[1]);
                        CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
                        CloudNet.getInstance().getServerGroups().remove(args[1]);
                        CloudNet.getInstance().getNetworkManager().updateAll();
                        for (MinecraftServer minecraftServer : CloudNet.getInstance().getServers(args[1])) {
                            minecraftServer.getWrapper().stopServer(minecraftServer);
                        }
                        sender.sendMessage("The group was successfully deleted");
                    } else {
                        sender.sendMessage("The server group doesn't exist");
                    }
                } else if (args[0].equalsIgnoreCase("proxyGroup")) {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1])) {
                        ProxyGroup proxyGroup = CloudNet.getInstance().getProxyGroup(args[1]);
                        CloudNet.getInstance().getConfig().deleteGroup(proxyGroup);
                        CloudNet.getInstance().getProxyGroups().remove(args[1]);
                        CloudNet.getInstance().getNetworkManager().updateAll();
                        for (ProxyServer proxyServer : CloudNet.getInstance().getProxys(args[1])) {
                            proxyServer.getWrapper().stopProxy(proxyServer);
                        }
                        sender.sendMessage("The group was successfully deleted");
                    } else {
                        sender.sendMessage("The proxy group doesn't exist");
                    }
                } else if (args[0].equalsIgnoreCase("wrapper")) {
                    if (CloudNet.getInstance().getWrappers().containsKey(args[1])) {
                        Wrapper wrapper = CloudNet.getInstance().getWrappers().get(args[1]);
                        CloudNet.getInstance()
                                .getConfig()
                                .getWrappers()
                                .stream()
                                .filter(wrapperMeta -> wrapperMeta.getId()
                                                                  .equals(wrapper.getName()))
                                .findFirst()
                                .ifPresent(CloudNet.getInstance().getConfig()::deleteWrapper);
                        sender.sendMessage("The wrapper was successfully deleted");
                    } else {
                        sender.sendMessage("The wrapper doesn't exist");
                    }
                }
                break;
            case 4:
                if (args[0].equalsIgnoreCase("template")) {
                    if (args[1].equalsIgnoreCase("serverGroup")) {
                        String name = args[3];
                        if (CloudNet.getInstance().getServerGroups().containsKey(args[2])) {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            if (serverGroup.getTemplates().stream().anyMatch(template -> template.getName().equalsIgnoreCase(name))) {
                                serverGroup.getTemplates().stream().filter(template -> template.getName().equalsIgnoreCase(name)).collect(
                                    Collectors.toList()).forEach(serverGroup.getTemplates()::remove);
                                CloudNet.getInstance().getConfig().createGroup(serverGroup);
                                CloudNet.getInstance().getNetworkManager().updateAll();
                                sender.sendMessage("The template was successfully deleted");
                            } else {
                                sender.sendMessage("That template does not exist for this server group");
                            }
                        } else {
                            sender.sendMessage("The server group doesn't exists");
                        }
                    }
                }
                break;
            default:
                sender.sendMessage("delete serverGroup <name>",
                                   "delete proxyGroup <name>",
                                   "delete wrapper <name>",
                                   "delete template serverGroup <group> <template>");
                break;
        }
    }
}
