package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandDelete extends Command implements TabCompletable {

    public CommandDelete() {
        super("delete", "cloudnet.command.delete");

        description = "Deletes a server group, proxy group, wrapper or templates of a server group";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        switch (parsedLine.words().size()) {
            case 3:
                String commandArgument = parsedLine.words().get(1);
                String secondCommandArgument = parsedLine.words().get(1);
                if (commandArgument.equalsIgnoreCase("serverGroup")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(secondCommandArgument)) {
                        ServerGroup serverGroup = CloudNet.getInstance().getServerGroup(secondCommandArgument);
                        CloudNet.getInstance().getConfig().deleteGroup(serverGroup);
                        CloudNet.getInstance().getServerGroups().remove(secondCommandArgument);
                        CloudNet.getInstance().getNetworkManager().updateAll();
                        for (MinecraftServer minecraftServer : CloudNet.getInstance().getServers(secondCommandArgument)) {
                            minecraftServer.getWrapper().stopServer(minecraftServer);
                        }
                        sender.sendMessage("§aThe group was successfully deleted");
                    } else {
                        sender.sendMessage("§cThe server group doesn't exist");
                    }
                } else if (commandArgument.equalsIgnoreCase("proxyGroup")) {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(secondCommandArgument)) {
                        ProxyGroup proxyGroup = CloudNet.getInstance().getProxyGroup(secondCommandArgument);
                        CloudNet.getInstance().getConfig().deleteGroup(proxyGroup);
                        CloudNet.getInstance().getProxyGroups().remove(secondCommandArgument);
                        CloudNet.getInstance().getNetworkManager().updateAll();
                        for (ProxyServer proxyServer : CloudNet.getInstance().getProxys(secondCommandArgument)) {
                            proxyServer.getWrapper().stopProxy(proxyServer);
                        }
                        sender.sendMessage("§aThe group was successfully deleted");
                    } else {
                        sender.sendMessage("§cThe proxy group doesn't exist");
                    }
                }
                break;
            case 5:
                commandArgument = parsedLine.words().get(1);
                secondCommandArgument = parsedLine.words().get(2);
                String thirdCommandArgument = parsedLine.words().get(3);
                String fourthCommandArgument = parsedLine.words().get(4);
                if (commandArgument.equalsIgnoreCase("template")) {
                    if (secondCommandArgument.equalsIgnoreCase("serverGroup")) {
                        if (CloudNet.getInstance().getServerGroups().containsKey(thirdCommandArgument)) {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(thirdCommandArgument);
                            if (serverGroup.getTemplates().stream().anyMatch(template -> template.getName().equalsIgnoreCase(
                                fourthCommandArgument))) {
                                serverGroup.getTemplates().stream().filter(template -> template.getName().equalsIgnoreCase(
                                    fourthCommandArgument)).collect(
                                    Collectors.toList()).forEach(serverGroup.getTemplates()::remove);
                                CloudNet.getInstance().getConfig().createGroup(serverGroup);
                                CloudNet.getInstance().getNetworkManager().updateAll();
                                sender.sendMessage("§aThe template was successfully deleted");
                            } else {
                                sender.sendMessage("§eThat template does not exist for this server group");
                            }
                        } else {
                            sender.sendMessage("§cThe server group doesn't exists");
                        }
                    }
                }
                break;
            default:
                sender.sendMessage("delete serverGroup <name>",
                                   "delete proxyGroup <name>",
                                   "delete template serverGroup <group> <template>");
                break;
        }
    }

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> candidates = new ArrayList<>();
        if (parsedLine.words().get(0).equalsIgnoreCase("delete") && parsedLine.words().size() == 1) {
            candidates.add(new Candidate("serverGroup", "serverGroup", null, "Deletes a server group", null, null,true));
            candidates.add(new Candidate("proxyGroup", "proxyGroup", null, "Deletes a proxy group", null, null,true));
            candidates.add(new Candidate("template", "template", null, "Deletes a server group template", null, null,true));
            return candidates;
        }
        if (parsedLine.words().get(0).equalsIgnoreCase("delete") && parsedLine.words().size() == 2 && parsedLine.words().get(1).equalsIgnoreCase("serverGroup")) {
            for (String group : CloudNet.getInstance().getServerGroups().keySet()) {
                candidates.add(new Candidate(group, group, null, "A server group", null, null,true));
            }
            return candidates;
        }
        if (parsedLine.words().get(0).equalsIgnoreCase("delete") && parsedLine.words().size() == 2 && parsedLine.words().get(1).equalsIgnoreCase("proxyGroup")) {
            for (String group : CloudNet.getInstance().getProxyGroups().keySet()) {
                candidates.add(new Candidate(group, group, null, "A proxy group", null, null,true));
            }
            return candidates;
        }
        if (parsedLine.words().get(0).equalsIgnoreCase("delete") && parsedLine.words().size() >= 2 && parsedLine.words().get(1).equalsIgnoreCase("template")) {
            if (parsedLine.words().size() == 2) {
                candidates.add(new Candidate("serverGroup", "serverGroup", null, "A sub argument", null, null,true));
                return candidates;
            } else if (parsedLine.words().get(2).equalsIgnoreCase("serverGroup") && parsedLine.words().size() >= 3) {
                if (parsedLine.words().get(2).equalsIgnoreCase("serverGroup") && parsedLine.words().size() == 4 && CloudNet.getInstance().getServerGroups().containsKey(parsedLine.words().get(3))) {
                    for (Template template : CloudNet.getInstance().getServerGroup(parsedLine.words().get(3)).getTemplates()) {
                        candidates.add(new Candidate(template.getName(), template.getName(), null, "A server group template", null, null,true));
                    }
                    return candidates;
                } else {
                    if (parsedLine.words().get(2).equalsIgnoreCase("serverGroup") && parsedLine.words().size() == 3) {
                        for (String group : CloudNet.getInstance().getServerGroups().keySet()) {
                            candidates.add(new Candidate(group, group, null, "A server group", null, null,true));
                        }
                        return candidates;
                    }
                }
            }
        }
        return candidates;
    }
}
