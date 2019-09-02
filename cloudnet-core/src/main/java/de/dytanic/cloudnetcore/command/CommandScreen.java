/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.command.TabCompletable;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.ArrayList;
import java.util.List;

public final class CommandScreen extends Command implements TabCompletable {

    public CommandScreen() {
        super("screen", "cloudnet.command.screen", "sc");

        description = "Shows you the console of one server";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {

        if (CloudNet.getInstance().getScreenProvider().getMainServiceId() != null && args.length > 1 && args[0].equalsIgnoreCase("write")) {
            ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
            StringBuilder stringBuilder = new StringBuilder();
            for (short i = 1; i < args.length; i++) {
                stringBuilder.append(args[i]).append(NetworkUtils.SPACE_STRING);
            }
            String commandLine = stringBuilder.substring(0, stringBuilder.length() - 1);
            Wrapper wrapper = CloudNet.getInstance().getWrappers().get(serviceId.getWrapperId());
            if (wrapper != null) {
                if (wrapper.getServers().containsKey(serviceId.getServerId())) {
                    wrapper.writeServerCommand(commandLine, wrapper.getServers().get(serviceId.getServerId()).getServerInfo());
                }
                if (wrapper.getProxys().containsKey(serviceId.getServerId())) {
                    wrapper.writeProxyCommand(commandLine, wrapper.getProxys().get(serviceId.getServerId()).getProxyInfo());
                }
            }
            return;
        }

        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("leave") && CloudNet.getInstance().getScreenProvider().getMainServiceId() != null) {

                    ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                    CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                    CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                    sender.sendMessage("You left the screen session");
                    return;
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("-s") || args[0].equalsIgnoreCase("server")) {

                    MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[1]);
                    if (minecraftServer != null) {

                        ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                        if (serviceId != null) {
                            CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                        }

                        minecraftServer.getWrapper().enableScreen(minecraftServer.getServerInfo());
                        sender.sendMessage("You joined the screen session of " + minecraftServer.getServerId());
                        CloudNet.getInstance().getScreenProvider().setMainServiceId(minecraftServer.getServiceId());
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("-p") || args[0].equalsIgnoreCase("proxy")) {

                    ProxyServer minecraftServer = CloudNet.getInstance().getProxy(args[1]);
                    if (minecraftServer != null) {
                        ServiceId serviceId = CloudNet.getInstance().getScreenProvider().getMainServiceId();
                        if (serviceId != null) {
                            CloudNet.getInstance().getScreenProvider().disableScreen(serviceId.getServerId());
                            CloudNet.getInstance().getScreenProvider().setMainServiceId(null);
                        }

                        minecraftServer.getWrapper().enableScreen(minecraftServer.getProxyInfo());
                        sender.sendMessage("You joined the screen session of " + minecraftServer.getServerId());
                        CloudNet.getInstance().getScreenProvider().setMainServiceId(minecraftServer.getServiceId());
                    }
                    return;
                }
                break;
            default:
                sender.sendMessage(
                    "screen server (-s) | proxy (-p) <name> | The output of the console of the service is transferred to the console of this instance",
                    "screen leave | The console output closes",
                    "screen write <command> | You write a command directly into the console of the service");
                break;
        }
    }

    @Override
    public List<String> onTab(long argsLength, String lastWord) {
        List<String> list = new ArrayList<>(CloudNet.getInstance().getServers().size() + CloudNet.getInstance().getProxys().size());

        list.addAll(CloudNet.getInstance().getServers().keySet());
        list.addAll(CloudNet.getInstance().getProxys().keySet());

        return list;
    }
}
