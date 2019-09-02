/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.user.BasicUser;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.setup.SetupProxyGroup;
import de.dytanic.cloudnetcore.setup.SetupServerGroup;
import de.dytanic.cloudnetcore.setup.SetupWrapper;

import java.util.Arrays;
import java.util.function.Consumer;

public final class CommandCreate extends Command {

    public CommandCreate() {
        super("create", "cloudnet.command.create", "start");

        description = "Creates new Wrapper, ServerGroup, PermissionGroup, ProxyGroup or custom server";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {

        if (args.length > 2) {
            if (args[0].equalsIgnoreCase("dispatchCommand")) {
                //create dispatchCommand name create
                StringBuilder builder = new StringBuilder();
                for (short i = 2; i < args.length; i++) {
                    builder.append(args[i]);
                }

                CloudNet.getInstance().getDbHandlers().getCommandDispatcherDatabase().appendCommand(args[1],
                                                                                                    builder.substring(0, (builder.substring(
                                                                                                        0)
                                                                                                                                 .endsWith(
                                                                                                                                     " ") ? builder
                                                                                                        .length() - 1 : builder.length())));
                sender.sendMessage("A dispatcher was created \"" + args[1] + "\": \"" + builder.substring(0) + '"');
                return;
            }
        }

        switch (args.length) {
            case 2:
                if (args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1])) {
                        CloudNet.getInstance().startProxy(CloudNet.getInstance().getProxyGroups().get(args[1]));
                        sender.sendMessage("Trying to startup a proxy server...");
                    } else {
                        sender.sendMessage("The proxy group doesn't exist");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1])) {
                        CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(args[1]));
                        sender.sendMessage("Trying to startup a game server...");
                    } else {
                        sender.sendMessage("The server group doesn't exists");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("wrapper") && !CloudNet.getInstance().getWrappers().containsKey(args[1])) {
                    new SetupWrapper(sender, args[1]);
                    return;
                }
                if (args[0].equalsIgnoreCase("serverGroup")) {
                    new SetupServerGroup(sender, args[1]);
                    return;
                }
                if (args[0].equalsIgnoreCase("proxyGroup")) {
                    new SetupProxyGroup(sender, args[1]);
                    return;
                }
                break;
            case 3:
                if ((args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(args[2])) {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1])) {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++) {
                            CloudNet.getInstance().startProxy(CloudNet.getInstance().getProxyGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        sender.sendMessage("Trying to startup a proxy server...");
                    } else {
                        sender.sendMessage("The proxy group doesn't exists");
                    }
                    return;
                }
                if ((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(args[2])) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1])) {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++) {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        sender.sendMessage("Trying to startup a game server...");
                    } else {
                        sender.sendMessage("The server group doesn't exists");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("user")) {
                    if (!CloudNet.getInstance().getUsers().contains(args[1])) {
                        User user = new BasicUser(args[1], args[2], Arrays.asList());
                        CloudNet.getInstance().getUsers().add(user);
                        CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                        sender.sendMessage("The user was created!");
                    } else {
                        sender.sendMessage("The user already exists!");
                    }
                }
                break;
            case 4:
                if ((args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(args[2])) {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1]) && CloudNet.getInstance().getWrappers().containsKey(
                        args[3])) {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++) {
                            CloudNet.getInstance().startProxy(CloudNet.getInstance().getWrappers().get(args[3]),
                                                              CloudNet.getInstance().getProxyGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        sender.sendMessage("Trying to startup a proxy server...");
                    } else {
                        sender.sendMessage("The proxy group or wrapper doesn't exists");
                    }
                    return;
                }
                if ((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(args[2])) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1]) && CloudNet.getInstance().getWrappers().containsKey(
                        args[3])) {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++) {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getWrappers().get(args[3]),
                                                                   CloudNet.getInstance().getServerGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        sender.sendMessage("Trying to startup a game server...");
                    } else {
                        sender.sendMessage("The server group or wrapper doesn't exists");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("cloudserver") || args[0].equalsIgnoreCase("cs")) {
                    if (NetworkUtils.checkIsNumber(args[2])) {
                        CloudNet.getInstance().startCloudServer(args[1], Integer.parseInt(args[2]), args[3].equalsIgnoreCase("true"));
                        sender.sendMessage("Trying to startup a cloud server...");
                    } else {
                        sender.sendMessage("Invalid argument!");
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("TEMPLATE")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[2])) {
                        if (args[3].equalsIgnoreCase("LOCAL")) {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            serverGroup.getTemplates().add(new Template(args[1],
                                                                        TemplateResource.LOCAL,
                                                                        null,
                                                                        new String[] {},
                                                                        Arrays.asList()));
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);

                            NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(),
                                                CloudNet.getInstance().getConfig().getServerGroups(),
                                                new Acceptable<ServerGroup>() {
                                                    @Override
                                                    public boolean isAccepted(ServerGroup value) {
                                                        return true;
                                                    }
                                                });

                            NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(),
                                                CloudNet.getInstance().getConfig().getProxyGroups(),
                                                new Acceptable<ProxyGroup>() {
                                                    @Override
                                                    public boolean isAccepted(ProxyGroup value) {
                                                        return true;
                                                    }
                                                });
                            CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                                @Override
                                public void accept(Wrapper wrapper) {
                                    wrapper.updateWrapper();
                                }
                            });
                            sender.sendMessage("The template was created and all wrappers were updated!");
                        }
                        if (args[3].equalsIgnoreCase("MASTER")) {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            serverGroup.getTemplates().add(new Template(args[1],
                                                                        TemplateResource.MASTER,
                                                                        null,
                                                                        new String[] {},
                                                                        Arrays.asList()));
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);

                            NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(),
                                                CloudNet.getInstance().getConfig().getServerGroups(),
                                                new Acceptable<ServerGroup>() {
                                                    @Override
                                                    public boolean isAccepted(ServerGroup value) {
                                                        return true;
                                                    }
                                                });

                            NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(),
                                                CloudNet.getInstance().getConfig().getProxyGroups(),
                                                new Acceptable<ProxyGroup>() {
                                                    @Override
                                                    public boolean isAccepted(ProxyGroup value) {
                                                        return true;
                                                    }
                                                });
                            CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                                @Override
                                public void accept(Wrapper wrapper) {
                                    wrapper.updateWrapper();
                                }
                            });
                            sender.sendMessage("The template was created and all wrappers were updated!");
                        }
                    } else {
                        sender.sendMessage("The server group doesn't exist");
                    }
                }
                break;
            case 5:
                if (args[0].equalsIgnoreCase("TEMPLATE")) {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[2])) {
                        if (args[3].equalsIgnoreCase("URL")) {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            serverGroup.getTemplates().add(new Template(args[1],
                                                                        TemplateResource.URL,
                                                                        args[4],
                                                                        new String[] {("-Dtest=true")},
                                                                        Arrays.asList()));
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);

                            NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(),
                                                CloudNet.getInstance().getConfig().getServerGroups(),
                                                new Acceptable<ServerGroup>() {
                                                    @Override
                                                    public boolean isAccepted(ServerGroup value) {
                                                        return true;
                                                    }
                                                });

                            NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(),
                                                CloudNet.getInstance().getConfig().getProxyGroups(),
                                                new Acceptable<ProxyGroup>() {
                                                    @Override
                                                    public boolean isAccepted(ProxyGroup value) {
                                                        return true;
                                                    }
                                                });
                            CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                                @Override
                                public void accept(Wrapper wrapper) {
                                    wrapper.updateWrapper();
                                }
                            });
                            sender.sendMessage("The template was created and all wrappers were updated!");
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
}
