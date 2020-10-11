/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
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
import eu.cloudnetservice.cloudnet.v2.master.setup.SetupWrapper;

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

    private static void startProxiesOnWrapper(final CommandSender sender, final String[] args) {
        if (CloudNet.getInstance().getProxyGroups().containsKey(args[1]) && CloudNet.getInstance().getWrappers().containsKey(args[3])) {
            for (short i = 0; i < Integer.parseInt(args[2]); i++) {
                CoreProxyProcessBuilder.create(args[1])
                                       .wrapperName(args[3])
                                       .startProxy();
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
                CoreServerProcessBuilder.create(args[1])
                                        .wrapperName(args[3])
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
}
