/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.user.BasicUser;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.setup.SetupProxyGroup;
import de.dytanic.cloudnetcore.setup.SetupServerGroup;
import de.dytanic.cloudnetcore.setup.SetupWrapper;

import java.util.Collections;

public final class CommandCreate extends Command {

    private static final String[] EMPTY_STRING_ARRAY = {};

    public CommandCreate() {
        super("create", "cloudnet.command.create", "start");
        description = "Creates new wrappers, server groups, permission groups, proxy groups or custom servers";
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        if (args.length > 2) {
            if (args[0].equalsIgnoreCase("dispatchCommand")) {
                dispatchCommand(sender, args);
                return;
            }
        }

        switch (args.length) {
            case 2:
                if (args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) {
                    startProxies(sender, args[1], 1);
                    break;
                }
                if (args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) {
                    startServers(sender, args[1], 1);
                    break;
                }
                if (args[0].equalsIgnoreCase("wrapper") && !CloudNet.getInstance().getWrappers().containsKey(args[1])) {
                    final SetupWrapper setupWrapper = new SetupWrapper(sender, args[1]);
                    setupWrapper.startSetup();
                    break;
                }
                if (args[0].equalsIgnoreCase("serverGroup")) {
                    final SetupServerGroup setupServerGroup = new SetupServerGroup(sender, args[1]);
                    setupServerGroup.startSetup();
                    break;
                }
                if (args[0].equalsIgnoreCase("proxyGroup")) {
                    final SetupProxyGroup setupProxyGroup = new SetupProxyGroup(sender, args[1]);
                    setupProxyGroup.startSetup();
                    break;
                }
                break;
            case 3:
                if ((args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(args[2])) {
                    startProxies(sender, args[1], Integer.parseInt(args[2]));
                    break;
                }
                if ((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(args[2])) {
                    startServers(sender, args[1], Integer.parseInt(args[2]));
                    break;
                }
                if (args[0].equalsIgnoreCase("user")) {
                    createUser(sender, args);
                }
                break;
            case 4:
                if ((args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(args[2])) {
                    startProxiesOnWrapper(sender, args);
                    break;
                }
                if ((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(args[2])) {
                    startServersOnWrapper(sender, args);
                    break;
                }
                if (args[0].equalsIgnoreCase("cloudserver") || args[0].equalsIgnoreCase("cs")) {
                    startCloudServer(sender, args);
                    break;
                }
                if (args[0].equalsIgnoreCase("template")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[2])) {
                        if (args[3].equalsIgnoreCase("local")) {
                            createTemplate(sender, TemplateResource.LOCAL, args[1], args[2]);
                        }
                        if (args[3].equalsIgnoreCase("master")) {
                            createTemplate(sender, TemplateResource.MASTER, args[1], args[2]);
                        }
                    } else {
                        sender.sendMessage("The server group doesn't exist");
                    }
                }
                break;
            case 5:
                if (args[0].equalsIgnoreCase("template")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[2])) {
                        if (args[3].equalsIgnoreCase("url")) {
                            createTemplate(sender, TemplateResource.URL, args[1], args[2], args[4]);
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
                                   "create CLOUDSERVER <name> <memory> <priorityStop>",
                                   "create USER <name> <password> | Creates a new user with specified name and password",
                                   "create PROXYGROUP <name> | Creates a completely new proxy group for BungeeCord with its own configurations, etc.",
                                   "create SERVERGROUP <name> | Creates a completely new server group for Minecraft servers with its own configurations, etc.",
                                   "create DISPATCHCOMMAND <main-command> <command> | Creates a simple command alias",
                                   "create WRAPPER <name> | Creates and whitelists a new wrapper. The wrapper can also have the same IP of a previous wrapper",
                                   "create TEMPLATE <name> <group> LOCAL | Creates a new locale (Wrapper locales) template for a server group",
                                   "create TEMPLATE <name> <group> MASTER | Creates a new master backend (Master locales) template for a server group",
                                   "create TEMPLATE <name> <group> URL <url> | Creates a new template of a server group via url");
                break;
        }
    }

    private static void dispatchCommand(final CommandSender sender, final String[] args) {
        //create dispatchCommand name create
        StringBuilder builder = new StringBuilder();
        for (short i = 2; i < args.length; i++) {
            builder.append(args[i]);
        }

        CloudNet.getInstance().getDbHandlers().getCommandDispatcherDatabase().appendCommand(args[1], builder.toString());
        sender.sendMessage(String.format("A dispatcher was created \"%s\": \"%s\"", args[1], builder));
    }

    private static void startProxies(final CommandSender sender, final String proxyGroup, int count) {
        if (CloudNet.getInstance().getProxyGroups().containsKey(proxyGroup)) {
            for (int i = 0; i < count; i++) {
                CloudNet.getInstance().startProxy(CloudNet.getInstance().getProxyGroups().get(proxyGroup));
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
                CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(serverGroup));
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("Trying to startup a game server...");
        } else {
            sender.sendMessage("The server group doesn't exists");
        }
    }

    private static void createUser(final CommandSender sender, final String[] args) {
        if (CloudNet.getInstance().getUser(args[1]) == null) {
            User user = new BasicUser(args[1], args[2], Collections.emptyList());
            CloudNet.getInstance().getUsers().add(user);
            CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            sender.sendMessage("The user was created!");
        } else {
            sender.sendMessage("The user already exists!");
        }
    }

    private static void startProxiesOnWrapper(final CommandSender sender, final String[] args) {
        if (CloudNet.getInstance().getProxyGroups().containsKey(args[1]) && CloudNet.getInstance().getWrappers().containsKey(args[3])) {
            for (short i = 0; i < Integer.parseInt(args[2]); i++) {
                CloudNet.getInstance().startProxy(
                    CloudNet.getInstance().getWrappers().get(args[3]),
                    CloudNet.getInstance().getProxyGroups().get(args[1]));
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("Trying to startup a proxy server...");
        } else {
            sender.sendMessage("The proxy group or wrapper doesn't exists");
        }
    }

    private static void startServersOnWrapper(final CommandSender sender, final String[] args) {
        if (CloudNet.getInstance().getServerGroups().containsKey(args[1]) && CloudNet.getInstance().getWrappers().containsKey(args[3])) {
            for (short i = 0; i < Integer.parseInt(args[2]); i++) {
                CloudNet.getInstance().startGameServer(
                    CloudNet.getInstance().getWrappers().get(args[3]),
                    CloudNet.getInstance().getServerGroups().get(args[1]));
                NetworkUtils.sleepUninterruptedly(2000L);
            }
            sender.sendMessage("Trying to startup a game server...");
        } else {
            sender.sendMessage("The server group or wrapper doesn't exists");
        }
    }

    private static void startCloudServer(final CommandSender sender, final String[] args) {
        if (NetworkUtils.checkIsNumber(args[2])) {
            CloudNet.getInstance().startCloudServer(args[1], Integer.parseInt(args[2]), args[3].equalsIgnoreCase("true"));
            sender.sendMessage("Trying to startup a cloud server...");
        } else {
            sender.sendMessage("Invalid argument!");
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
}
