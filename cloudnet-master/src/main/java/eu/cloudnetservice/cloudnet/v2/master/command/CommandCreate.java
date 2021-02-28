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
        if (parsedLine.words().size() > 2) {
            if (parsedLine.words().get(0).equalsIgnoreCase("dispatchCommand")) {
                dispatchCommand(sender, parsedLine);
                return;
            }
        }

        switch (parsedLine.words().size()) {
            case 3:
                if (parsedLine.words().get(1).equalsIgnoreCase("proxy") || parsedLine.words().get(1).equalsIgnoreCase("-p")) {
                    startProxies(sender, parsedLine.words().get(2), 1);
                    break;
                }
                if (parsedLine.words().get(1).equalsIgnoreCase("server") || parsedLine.words().get(1).equalsIgnoreCase("-s")) {
                    startServers(sender, parsedLine.words().get(2), 1);
                    break;
                }
                if (parsedLine.words().get(1).equalsIgnoreCase("serverGroup")) {
                    CloudNet.getInstance().getConsoleRegistry().registerInput(new SetupServerGroup(sender, parsedLine.words().get(2)));
                    CloudNet.getInstance().getConsoleManager().changeConsoleInput(SetupServerGroup.class);
                    break;
                }
                if (parsedLine.words().get(1).equalsIgnoreCase("proxyGroup")) {
                    CloudNet.getInstance().getConsoleRegistry().registerInput(new SetupProxyGroup(sender, parsedLine.words().get(2)));
                    CloudNet.getInstance().getConsoleManager().changeConsoleInput(SetupProxyGroup.class);
                    break;
                }
                break;
            case 4:
                if ((parsedLine.words().get(1).equalsIgnoreCase("proxy") || parsedLine.words().get(1).equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(parsedLine.words().get(3))) {
                    startProxies(sender, parsedLine.words().get(2), Integer.parseInt(parsedLine.words().get(3)));
                    break;
                }
                if ((parsedLine.words().get(1).equalsIgnoreCase("server") || parsedLine.words().get(1).equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(parsedLine.words().get(3))) {
                    startServers(sender, parsedLine.words().get(2), Integer.parseInt(parsedLine.words().get(3)));
                    break;
                }
                break;
            case 5:
                if ((parsedLine.words().get(1).equalsIgnoreCase("proxy") || parsedLine.words().get(1).equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(parsedLine.words().get(3))) {
                    startProxiesOnWrapper(sender, parsedLine);
                    break;
                }
                if ((parsedLine.words().get(1).equalsIgnoreCase("server") || parsedLine.words().get(1).equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(parsedLine.words().get(3))) {
                    startServersOnWrapper(sender, parsedLine);
                    break;
                }
                if (parsedLine.words().get(1).equalsIgnoreCase("template")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(parsedLine.words().get(3))) {
                        if (parsedLine.words().get(4).equalsIgnoreCase("local")) {
                            createTemplate(sender, TemplateResource.LOCAL, parsedLine.words().get(2), parsedLine.words().get(3));
                        }
                        if (parsedLine.words().get(4).equalsIgnoreCase("master")) {
                            createTemplate(sender, TemplateResource.MASTER, parsedLine.words().get(2), parsedLine.words().get(3));
                        }
                    } else {
                        sender.sendMessage("The server group doesn't exist");
                    }
                }
                break;
            case 6:
                if (parsedLine.words().get(1).equalsIgnoreCase("template")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(parsedLine.words().get(3))) {
                        if (parsedLine.words().get(4).equalsIgnoreCase("url")) {
                            createTemplate(sender, TemplateResource.URL, parsedLine.words().get(2), parsedLine.words().get(3), parsedLine.words().get(5));
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
                                   "create DISPATCHCOMMAND <main-command> <command> | Creates a simple command alias",
                                   "create TEMPLATE <name> <group> LOCAL | Creates a new locale (Wrapper locales) template for a server group",
                                   "create TEMPLATE <name> <group> MASTER | Creates a new master backend (Master locales) template for a server group",
                                   "create TEMPLATE <name> <group> URL <url> | Creates a new template of a server group via url");
                break;
        }
    }

    private static void dispatchCommand(final CommandSender sender, ParsedLine parsedLine) {
        //create dispatchCommand name create
        StringBuilder builder = new StringBuilder();
        for (short i = 2; i < parsedLine.words().size(); i++) {
            builder.append(parsedLine.words().get(i));
        }

        CloudNet.getInstance().getDbHandlers().getCommandDispatcherDatabase().appendCommand(parsedLine.words().get(1), builder.toString());
        sender.sendMessage(String.format("A dispatcher was created \"%s\": \"%s\"", parsedLine.words().get(1), builder));
    }

    private static void startProxies(final CommandSender sender, final String proxyGroup, int count) {
        if (CloudNet.getInstance().getProxyGroups().containsKey(proxyGroup)) {
            for (int i = 0; i < count; i++) {
                CoreProxyProcessBuilder.create(proxyGroup).startProxy();
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("Trying to startup a proxy server...");
        } else {
            sender.sendMessage("The proxy group doesn't exists");
        }
    }

    private static void startServers(final CommandSender sender, final String serverGroup, final int count) {
        if (CloudNet.getInstance().getServerGroups().containsKey(serverGroup)) {
            for (short i = 0; i < count; i++) {
                CoreServerProcessBuilder.create(serverGroup)
                                        .startServer();
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("Trying to startup a game server...");
        } else {
            sender.sendMessage("The server group doesn't exists");
        }
    }

    private static void startProxiesOnWrapper(final CommandSender sender, ParsedLine parsedLine) {
        if (CloudNet.getInstance().getProxyGroups().containsKey(parsedLine.words().get(1)) && CloudNet.getInstance().getWrappers().containsKey(parsedLine.words().get(3))) {
            for (short i = 0; i < Integer.parseInt(parsedLine.words().get(2)); i++) {
                CoreProxyProcessBuilder.create(parsedLine.words().get(1))
                                       .wrapperName(parsedLine.words().get(3))
                                       .startProxy();
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("Trying to startup a proxy server...");
        } else {
            sender.sendMessage("The proxy group or wrapper doesn't exists");
        }
    }

    private static void startServersOnWrapper(final CommandSender sender, ParsedLine parsedLine) {
        if (CloudNet.getInstance().getServerGroups().containsKey(parsedLine.words().get(1)) && CloudNet.getInstance().getWrappers().containsKey(parsedLine.words().get(3))) {
            for (short i = 0; i < Integer.parseInt(parsedLine.words().get(2)); i++) {
                CoreServerProcessBuilder.create(parsedLine.words().get(1))
                                        .wrapperName(parsedLine.words().get(3))
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
        sender.sendMessage("The template was created and all wrappers were updated!");
    }

    @Override
    public List<Candidate> onTab(final ParsedLine parsedLine) {
        List<Candidate> candidates = new ArrayList<>();
        if (parsedLine.word().equalsIgnoreCase("create")) {
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
        if (parsedLine.word().equalsIgnoreCase("PROXY")) {
            candidates.add(new Candidate("<proxyGroup>", "<proxyGroup>", null,"Creates a proxy server of a proxy group. <count> is not mandatory", null, null,true));
        }
        return candidates;
    }
}
