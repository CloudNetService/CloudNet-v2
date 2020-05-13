package eu.cloudnetservice.v2.master.command;

import eu.cloudnetservice.v2.command.Command;
import eu.cloudnetservice.v2.command.CommandSender;
import eu.cloudnetservice.v2.lib.server.ServerGroup;
import eu.cloudnetservice.v2.lib.server.template.Template;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

import java.util.HashSet;

/**
 * Created by Tareko on 28.08.2017.
 */
public final class CommandCopy extends Command {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public CommandCopy() {
        super("copy", "cloudnet.command.copy");

        description = "Copies a game server to the template which it loaded from";

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
                break;
            }
            case 2: {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
                if (minecraftServer != null) {
                    ServerGroup serverGroup = minecraftServer.getGroup();
                    if (serverGroup != null) {
                        Template template = serverGroup.getTemplates().stream()
                                                       .filter(t -> t.getName().equalsIgnoreCase(args[1]))
                                                       .findFirst()
                                                       .orElse(null);

                        if (template == null) {
                            template = new Template(args[1],
                                                    minecraftServer.getProcessMeta().getTemplate().getBackend(),
                                                    minecraftServer.getProcessMeta().getTemplate().getUrl(),
                                                    EMPTY_STRING_ARRAY,
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
                break;
            }
            default: {
                sender.sendMessage("copy <server> | Copies the current server as a local template to the wrapper of the instance");
                sender.sendMessage(
                    "copy <server> <template> | Copies the current server as a local template to the wrapper of the instance which you set");
                break;
            }
        }
    }
}
