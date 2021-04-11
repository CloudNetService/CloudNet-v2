package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class CommandCopy extends Command implements TabCompletable {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public CommandCopy() {
        super("copy", "cloudnet.command.copy");

        description = "Copies a game server to the template which it loaded from";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        switch (parsedLine.words().size()) {
            case 2: {
                String commandArgument = parsedLine.words().get(1);
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(commandArgument);
                if (minecraftServer != null) {
                    minecraftServer.getWrapper().copyServer(minecraftServer.getServerInfo());
                    sender.sendMessage("§aThe server " + commandArgument + " was copied");
                } else {
                    sender.sendMessage("§cThe specified server doesn't exist");
                }
                break;
            }
            case 3: {
                String commandArgument = parsedLine.words().get(1);
                String secondCommandArgument = parsedLine.words().get(1);
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(commandArgument);
                if (minecraftServer != null) {
                    ServerGroup serverGroup = minecraftServer.getGroup();
                    if (serverGroup != null) {
                        Template template = serverGroup.getTemplates().stream()
                                                       .filter(t -> t.getName().equalsIgnoreCase(secondCommandArgument))
                                                       .findFirst()
                                                       .orElse(null);

                        if (template == null) {
                            template = new Template(commandArgument,
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
                        sender.sendMessage("§aCreating Template \"" + template.getName() + "\" for " + serverGroup.getName() + " and copying server " + minecraftServer
                            .getServiceId()
                            .getServerId() + "...");
                    } else {
                        sender.sendMessage("§cThe group doesn't exist");
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

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> candidates = new ArrayList<>();
        if (parsedLine.words().size() == 1 && parsedLine.words().get(0).equalsIgnoreCase("copy")) {
            for (MinecraftServer server : CloudNet.getInstance().getServers().values()) {
                candidates.add(new Candidate(server.getName(), server.getName(), server.getGroup().getName(), "A server", null, null,true));
            }
        }
        return candidates;
    }
}
