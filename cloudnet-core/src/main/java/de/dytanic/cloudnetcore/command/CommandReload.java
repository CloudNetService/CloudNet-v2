/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public final class CommandReload extends Command {

    public CommandReload() {
        super("reload", "cloudnet.command.reload", "rl");

        description = "Reloads the config and modules";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("all")) {
                sender.sendMessage("[RELOAD] Trying to reload CloudNet...");
                try {
                    CloudNet.getInstance().reload();
                    sender.sendMessage("[RELOAD] Reloading was completed successfully!");
                } catch (Exception e) {
                    sender.sendMessage("[RELOAD] Failed to reload CloudNet");
                    e.printStackTrace();
                }
            }
            if (args[0].equalsIgnoreCase("config")) {
                sender.sendMessage("[RELOAD] Trying to reload config");
                reloadConfig();
                sender.sendMessage("[RELOAD] Reloading was completed successfully");
            }
            if (args[0].equalsIgnoreCase("wrapper")) {
                for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                    if (wrapper.getChannel() != null) {
                        wrapper.writeCommand("reload");
                    }
                }
            }
        } else {
            sender.sendMessage("reload ALL | Loads all groups as well as modules, permissions, etc.",
                               "reload CONFIG | Reload the configuration file, and its server groups etc.",
                               "reload WRAPPER | Dispatched on all wrappers the command \"reload\"");
        }
    }

    public static void reloadConfig() {
        try {
            CloudNet.getInstance().getConfig().load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CloudNet.getInstance().getServerGroups().clear();
        CloudNet.getInstance().getProxyGroups().clear();
        CloudNet.getInstance().getUsers().clear();
        CloudNet.getInstance().getUsers().addAll(CloudNet.getInstance().getConfig().getUsers());

        CloudNet.getInstance().getServerGroups().putAll(CloudNet.getInstance().getConfig().getServerGroups());
        CloudNet.getInstance().getServerGroups().forEach((name, serverGroup) -> {
            CloudNet.getLogger().info(String.format("Loading server group: %s", serverGroup.getName()));
            CloudNet.getInstance().setupGroup(serverGroup);
        });

        CloudNet.getInstance().getProxyGroups().putAll(CloudNet.getInstance().getConfig().getProxyGroups());
        CloudNet.getInstance().getProxyGroups().forEach((name, proxyGroup) -> {
            CloudNet.getLogger().info(String.format("Loading proxy group: %s", proxyGroup.getName()));
            CloudNet.getInstance().setupProxy(proxyGroup);
        });

        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll();
        CloudNet.getInstance().getWrappers().values().forEach(Wrapper::updateWrapper);
    }
}
