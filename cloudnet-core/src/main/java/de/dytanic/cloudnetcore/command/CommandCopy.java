/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.HashSet;

/**
 * Created by Tareko on 28.08.2017.
 */
public final class CommandCopy extends Command {

    public CommandCopy() {
        super("copy", "cloudnet.command.copy");

        description = "Copies a minecraft server to a template which is loaded local";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1: {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
                if (minecraftServer != null) {
                    minecraftServer.getWrapper().copyServer(minecraftServer.getServerInfo());
                    sender.sendMessage("The server " + args[0] + " was copied");
                } else {
                    sender.sendMessage("The specified server doesn't exist");
                }
            }
            break;
            case 2: {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
                if (minecraftServer != null) {
                    ServerGroup serverGroup = minecraftServer.getGroup();
                    if (serverGroup != null) {
                        Template template = CollectionWrapper.filter(serverGroup.getTemplates(), new Acceptable<Template>() {
                            @Override
                            public boolean isAccepted(Template template) {
                                return template.getName().equalsIgnoreCase(args[1]);
                            }
                        });
                        if (template == null) {
                            template = new Template(args[1],
                                                    minecraftServer.getProcessMeta().getTemplate().getBackend(),
                                                    minecraftServer.getProcessMeta().getTemplate().getUrl(),
                                                    new String[0],
                                                    new HashSet<>());
                            serverGroup.getTemplates().add(template);
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);
                            CloudNet.getInstance().getNetworkManager().updateAll();
                            for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                                wrapper.updateWrapper();
                            }
                        }
                        minecraftServer.getWrapper().copyServer(minecraftServer.getServerInfo());
                        sender.sendMessage("Creating Template \"" + template.getName() + "\" for " + serverGroup.getName() + " and copying server " + minecraftServer
                            .getServiceId()
                            .getServerId() + "...");
                    } else {
                        sender.sendMessage("The group doesn't exist");
                    }
                    return;
                }
            }
            break;
            default:
                sender.sendMessage("copy <server> | Copies the current server as a local template to the wrapper of the instance");
                sender.sendMessage(
                    "copy <server> <template> | Copies the current server as a local template to the wrapper of the instance which you set");
                break;
        }
    }
}
