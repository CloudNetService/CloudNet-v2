package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.ParsedLine;

import java.util.stream.Collectors;

public final class CommandDelete extends Command {

    public CommandDelete() {
        super("delete", "cloudnet.command.delete");

        description = "Deletes a server group, proxy group, wrapper or templates of a server group";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        switch (parsedLine.wordIndex()) {
            case 2:
                if (parsedLine.words().get(0).equalsIgnoreCase("serverGroup")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(parsedLine.words().get(1))) {
                        ServerGroup serverGroup = CloudNet.getInstance().getServerGroup(parsedLine.words().get(1));
                        CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
                        CloudNet.getInstance().getServerGroups().remove(parsedLine.words().get(1));
                        CloudNet.getInstance().getNetworkManager().updateAll();
                        for (MinecraftServer minecraftServer : CloudNet.getInstance().getServers(parsedLine.words().get(1))) {
                            minecraftServer.getWrapper().stopServer(minecraftServer);
                        }
                        sender.sendMessage("The group was successfully deleted");
                    } else {
                        sender.sendMessage("The server group doesn't exist");
                    }
                } else if (parsedLine.words().get(0).equalsIgnoreCase("proxyGroup")) {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(parsedLine.words().get(1))) {
                        ProxyGroup proxyGroup = CloudNet.getInstance().getProxyGroup(parsedLine.words().get(1));
                        CloudNet.getInstance().getConfig().deleteGroup(proxyGroup);
                        CloudNet.getInstance().getProxyGroups().remove(parsedLine.words().get(1));
                        CloudNet.getInstance().getNetworkManager().updateAll();
                        for (ProxyServer proxyServer : CloudNet.getInstance().getProxys(parsedLine.words().get(1))) {
                            proxyServer.getWrapper().stopProxy(proxyServer);
                        }
                        sender.sendMessage("The group was successfully deleted");
                    } else {
                        sender.sendMessage("The proxy group doesn't exist");
                    }
                } else if (parsedLine.words().get(0).equalsIgnoreCase("wrapper")) {
                    if (CloudNet.getInstance().getWrappers().containsKey(parsedLine.words().get(1))) {
                        final Wrapper wrapper = CloudNet.getInstance().getWrappers().get(parsedLine.words().get(1));
                        CloudNet.getInstance()
                                .getConfig()
                                .getWrappers()
                                .stream()
                                .filter(wrapperMeta -> wrapperMeta.getId().equals(wrapper.getName()))
                                .findFirst()
                                .ifPresent(CloudNet.getInstance().getConfig()::deleteWrapper);
                        sender.sendMessage("The wrapper was successfully deleted");
                    } else {
                        sender.sendMessage("The wrapper doesn't exist");
                    }
                }
                break;
            case 4:
                if (parsedLine.words().get(0).equalsIgnoreCase("template")) {
                    if (parsedLine.words().get(1).equalsIgnoreCase("serverGroup")) {
                        String name = parsedLine.words().get(3);
                        if (CloudNet.getInstance().getServerGroups().containsKey(parsedLine.words().get(2))) {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(parsedLine.words().get(2));
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
