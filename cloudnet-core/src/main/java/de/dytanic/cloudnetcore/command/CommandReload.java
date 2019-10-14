/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.function.Consumer;

public final class CommandReload extends Command {

    public CommandReload() {
        super("reload", "cloudnet.command.reload", "rl");

        description = "Reloads the config and modules";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("all")) {
                    sender.sendMessage("[RELOAD] Trying to reload CloudNet...");
                    try {
                        CloudNet.getInstance().reload();
                        sender.sendMessage("[RELOAD] Reloading was completed successfully!");
                    } catch (Exception e) {
                        sender.sendMessage("[RELOAD] Failed to reload CloudNet");
                        e.printStackTrace();
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("config")) {
                    sender.sendMessage("[RELOAD] Trying to reload config");
                    try {
                        CloudNet.getInstance().getConfig().load();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    CloudNet.getInstance().getServerGroups().clear();
                    CloudNet.getInstance().getProxyGroups().clear();
                    CloudNet.getInstance().getUsers().clear();
                    CloudNet.getInstance().getUsers().addAll(CloudNet.getInstance().getConfig().getUsers());

                    NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(),
                                        CloudNet.getInstance().getConfig().getServerGroups(),
                                        new Acceptable<ServerGroup>() {
                                            @Override
                                            public boolean isAccepted(ServerGroup value) {
                                                System.out.println("Loading ServerGroup: " + value.getName());
                                                CloudNet.getInstance().setupGroup(value);
                                                return true;
                                            }
                                        });

                    NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(),
                                        CloudNet.getInstance().getConfig().getProxyGroups(),
                                        new Acceptable<ProxyGroup>() {

                                            public boolean isAccepted(ProxyGroup value) {
                                                System.out.println("Loading ProxyGroup: " + value.getName());
                                                CloudNet.getInstance().setupProxy(value);
                                                return true;
                                            }
                                        });

                    CloudNet.getInstance().getNetworkManager().reload();
                    CloudNet.getInstance().getNetworkManager().updateAll();
                    CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                        @Override
                        public void accept(Wrapper wrapper) {
                            wrapper.updateWrapper();
                        }
                    });
                    sender.sendMessage("[RELOAD] Reloading was completed successfully");
                }
                if (args[0].equalsIgnoreCase("wrapper")) {
                    for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                        if (wrapper.getChannel() != null) {
                            wrapper.writeCommand("reload");
                        }
                    }
                }
                break;
            default:
                sender.sendMessage("reload ALL | Loads all groups as well as modules, permissions, etc.",
                                   "reload CONFIG | Reload the configuration file, and its server groups etc.",
                                   "reload WRAPPER | Dispatched on all wrappers the command \"reload\"");
                break;
        }
    }
}
