package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.TemplateResource;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import eu.cloudnetservice.cloudnet.v2.master.process.CoreProxyProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.master.process.CoreServerProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.master.setup.SetupProxyGroup;
import eu.cloudnetservice.cloudnet.v2.master.setup.SetupServerGroup;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandCreate extends Command implements TabCompletable {

    private static final String[] EMPTY_STRING_ARRAY = {};

    public CommandCreate() {
        super("create", "cloudnet.command.create", "start");
        description = "Creates new wrappers, server groups, permission groups, proxy groups or custom servers";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        switch (parsedLine.words().size()) {
            case 3:
                String commandArgument = parsedLine.words().get(1);
                String secondCommandArgument = parsedLine.words().get(2);
                if (commandArgument.equalsIgnoreCase("proxy") || commandArgument.equalsIgnoreCase("-p")) {
                    startProxies(sender, secondCommandArgument, 1);
                    break;
                }
                if (commandArgument.equalsIgnoreCase("server") || commandArgument.equalsIgnoreCase("-s")) {
                    startServers(sender, secondCommandArgument, 1);
                    break;
                }
                if (commandArgument.equalsIgnoreCase("serverGroup")) {
                    CloudNet.getInstance().getConsoleRegistry().registerInput(new SetupServerGroup(sender, secondCommandArgument));
                    CloudNet.getInstance().getConsoleManager().changeConsoleInput(SetupServerGroup.class);
                    break;
                }
                if (commandArgument.equalsIgnoreCase("proxyGroup")) {
                    CloudNet.getInstance().getConsoleRegistry().registerInput(new SetupProxyGroup(sender, secondCommandArgument));
                    CloudNet.getInstance().getConsoleManager().changeConsoleInput(SetupProxyGroup.class);
                    break;
                }
                break;
            case 4:
                commandArgument = parsedLine.words().get(1);
                secondCommandArgument = parsedLine.words().get(2);
                String thirdCommandArgument = parsedLine.words().get(3);
                if ((commandArgument.equalsIgnoreCase("proxy") || commandArgument.equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(thirdCommandArgument)) {
                    startProxies(sender, secondCommandArgument, Integer.parseInt(thirdCommandArgument));
                    break;
                }
                if ((commandArgument.equalsIgnoreCase("server") || commandArgument.equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(thirdCommandArgument)) {
                    startServers(sender, secondCommandArgument, Integer.parseInt(thirdCommandArgument));
                    break;
                }
                break;
            case 5:
                commandArgument = parsedLine.words().get(1);
                secondCommandArgument = parsedLine.words().get(2);
                thirdCommandArgument = parsedLine.words().get(3);
                String fourthCommandArgument = parsedLine.words().get(4);
                if ((commandArgument.equalsIgnoreCase("proxy") || commandArgument.equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(thirdCommandArgument)) {
                    startProxiesOnWrapper(sender, parsedLine);
                    break;
                }
                if ((commandArgument.equalsIgnoreCase("server") || commandArgument.equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(thirdCommandArgument)) {
                    startServersOnWrapper(sender, parsedLine);
                    break;
                }
                if (commandArgument.equalsIgnoreCase("template")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(thirdCommandArgument)) {
                        if (fourthCommandArgument.equalsIgnoreCase("local")) {
                            createTemplate(sender, TemplateResource.LOCAL, secondCommandArgument, thirdCommandArgument);
                        }
                        if (fourthCommandArgument.equalsIgnoreCase("master")) {
                            createTemplate(sender, TemplateResource.MASTER, secondCommandArgument, thirdCommandArgument);
                        }
                    } else {
                        sender.sendMessage("The server group doesn't exist");
                    }
                }
                break;
            case 6:
                commandArgument = parsedLine.words().get(1);
                secondCommandArgument = parsedLine.words().get(2);
                thirdCommandArgument = parsedLine.words().get(3);
                fourthCommandArgument = parsedLine.words().get(4);
                String fifthCommandArgument = parsedLine.words().get(5);
                if (commandArgument.equalsIgnoreCase("template")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(thirdCommandArgument)) {
                        if (fourthCommandArgument.equalsIgnoreCase("url")) {
                            createTemplate(sender, TemplateResource.URL, secondCommandArgument, thirdCommandArgument, fifthCommandArgument);
                        }
                    } else {
                        sender.sendMessage("The server group doesn't exists");
                    }
                }
                break;
            default:
                sender.sendMessage("create PROXY <proxyGroup> <count> | Creates a proxy server of a proxy group. <count> is not mandatory",
                                   "create PROXY <proxyGroup> <count> <wrapper> | Creates a proxy server of a proxy group. <count> is not mandatory",
                                   "create SERVER <serverGroup> <count> | Creates a game server of a server group. <count> is not mandatory",
                                   "create SERVER <serverGroup> <count> <wrapper> | Creates a game server of a server group. <count> is not mandatory",
                                   "create PROXYGROUP <name> | Creates a completely new proxy group for BungeeCord with its own configurations, etc.",
                                   "create SERVERGROUP <name> | Creates a completely new server group for Minecraft servers with its own configurations, etc.",
                                   "create TEMPLATE <name> <group> LOCAL | Creates a new locale (Wrapper locales) template for a server group",
                                   "create TEMPLATE <name> <group> MASTER | Creates a new master backend (Master locales) template for a server group",
                                   "create TEMPLATE <name> <group> URL <url> | Creates a new template of a server group via url");
                break;
        }
    }

    private static void startProxies(final CommandSender sender, final String proxyGroup, int count) {
        if (CloudNet.getInstance().getProxyGroups().containsKey(proxyGroup)) {
            for (int i = 0; i < count; i++) {
                CoreProxyProcessBuilder.create(proxyGroup).startProxy();
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("§eTrying to startup a proxy server...");
        } else {
            sender.sendMessage("§cThe proxy group doesn't exists");
        }
    }

    private static void startServers(final CommandSender sender, final String serverGroup, final int count) {
        if (CloudNet.getInstance().getServerGroups().containsKey(serverGroup)) {
            for (short i = 0; i < count; i++) {
                CoreServerProcessBuilder.create(serverGroup)
                                        .startServer();
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("§eTrying to startup a game server...");
        } else {
            sender.sendMessage("§cThe server group doesn't exists");
        }
    }

    private static void startProxiesOnWrapper(final CommandSender sender, ParsedLine parsedLine) {
        String commandArgument = parsedLine.words().get(1);
        String secondCommandArgument = parsedLine.words().get(2);
        String thirdCommandArgument = parsedLine.words().get(3);

        if (CloudNet.getInstance().getProxyGroups().containsKey(commandArgument) && CloudNet.getInstance().getWrappers().containsKey(thirdCommandArgument)) {
            for (short i = 0; i < Integer.parseInt(secondCommandArgument); i++) {
                CoreProxyProcessBuilder.create(commandArgument)
                                       .wrapperName(thirdCommandArgument)
                                       .startProxy();
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("§eTrying to startup a proxy server...");
        } else {
            sender.sendMessage("§cThe proxy group or wrapper doesn't exists");
        }
    }

    private static void startServersOnWrapper(final CommandSender sender, ParsedLine parsedLine) {
        String commandArgument = parsedLine.words().get(1);
        String secondCommandArgument = parsedLine.words().get(2);
        String thirdCommandArgument = parsedLine.words().get(3);
        if (CloudNet.getInstance().getServerGroups().containsKey(commandArgument) && CloudNet.getInstance().getWrappers().containsKey(thirdCommandArgument)) {
            for (short i = 0; i < Integer.parseInt(secondCommandArgument); i++) {
                CoreServerProcessBuilder.create(commandArgument)
                                        .wrapperName(thirdCommandArgument)
                                        .startServer();
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("Trying to startup a game server...");
        } else {
            sender.sendMessage("The server group or wrapper doesn't exists");
        }
    }

    private static void createTemplate(final CommandSender sender,
                                       final TemplateResource templateResource,
                                       final String templateName,
                                       final String serverGroup) {
        createTemplate(sender, templateResource, templateName, serverGroup, null);
    }

    private static void createTemplate(final CommandSender sender,
                                       final TemplateResource templateResource,
                                       final String templateName,
                                       final String serverGroupName,
                                       final String url) {
        ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(serverGroupName);
        serverGroup.getTemplates().add(
            new Template(templateName,
                         templateResource,
                         url,
                         EMPTY_STRING_ARRAY,
                         Collections.emptyList()));
        CloudNet.getInstance().getConfig().createGroup(serverGroup);

        CloudNet.getInstance().getServerGroups().putAll(CloudNet.getInstance().getConfig().getServerGroups());
        CloudNet.getInstance().getProxyGroups().putAll(CloudNet.getInstance().getConfig().getProxyGroups());

        CloudNet.getInstance().getWrappers().values().forEach(Wrapper::updateWrapper);
        sender.sendMessage("§aThe template was created and all wrappers were updated!");
    }

    @Override
    public List<Candidate> onTab(final ParsedLine parsedLine) {
        List<Candidate> candidates = new ArrayList<>();
        switch (parsedLine.words().size()) {
            case 1: {
                candidates.add(new Candidate("proxy", "PROXY", null,"Creates a proxy server of a proxy group. <count> is not mandatory", null, null,true));
                candidates.add(new Candidate("server", "SERVER", null,"Creates a game server of a server group. <count> is not mandatory", null, null,true));
                candidates.add(new Candidate("PROXYGROUP", "PROXYGROUP", null,"Creates a completely new proxy group for BungeeCord with its own configurations, etc.", null, null,true));
                candidates.add(new Candidate("SERVERGROUP", "SERVERGROUP", null,"Creates a completely new server group for Minecraft servers with its own configurations, etc.", null, null,true));
                candidates.add(new Candidate("DISPATCHCOMMAND", "DISPATCHCOMMAND", null,"Creates a simple command alias", null, null,true));
                candidates.add(new Candidate("WRAPPER", "WRAPPER", null,"Creates and whitelists a new wrapper. The wrapper can also have the same IP of a previous wrapper", null, null,true));
                candidates.add(new Candidate("TEMPLATE", "TEMPLATE", null,"Creates a new locale (Wrapper locales) template for a server group", null, null,true));
                candidates.add(new Candidate("TEMPLATE", "TEMPLATE", null,"Creates a new master backend (Master locales) template for a server group", null, null,true));
                candidates.add(new Candidate("TEMPLATE", "TEMPLATE", null,"Creates a new template of a server group via url", null, null,true));
                return candidates;
            }
            case 2: {
                if (parsedLine.words().get(1).equalsIgnoreCase("proxy")) {
                    for (String group : CloudNet.getInstance().getProxyGroups().keySet()) {
                        candidates.add(new Candidate(group, group, null,"Crates a proxy based on they group",null,null, false));
                    }
                    return candidates;
                }
                if (parsedLine.words().get(1).equalsIgnoreCase("server")) {
                    for (String group : CloudNet.getInstance().getServerGroups().keySet()) {
                        candidates.add(new Candidate(group, group, null,"Crates a server based on they group",null,null, false));
                    }
                    return candidates;
                }
            }
            case 3: {
                if (parsedLine.words().get(1).equalsIgnoreCase("template")) {
                    for (String group : CloudNet.getInstance().getServerGroups().keySet()) {
                        candidates.add(new Candidate(group, group, null,"A server group",null,null, false));
                    }
                    return candidates;
                }
            }
            case 4: {
                if (parsedLine.words().get(1).equalsIgnoreCase("template") && CloudNet.getInstance().getServerGroups().containsKey(parsedLine.words().get(3))) {
                    candidates.add(new Candidate("LOCAL", "LOCAL", null,"Local Backend",null,null, false));
                    candidates.add(new Candidate("Master", "Master", null,"Master Backend",null,null, false));
                    candidates.add(new Candidate("URL", "URL", null,"URL Backend",null,null, false));
                    return candidates;
                }
            }
        }
        return candidates;
    }
}
