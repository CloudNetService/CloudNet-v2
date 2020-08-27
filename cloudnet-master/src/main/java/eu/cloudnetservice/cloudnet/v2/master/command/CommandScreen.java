package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandScreen extends Command implements TabCompletable {

    public CommandScreen() {
        super("screen", "cloudnet.command.screen", "sc");

        description = "Shows you the console of one server";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine, String[] args) {

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
                if (wrapper.getProxies().containsKey(serviceId.getServerId())) {
                    wrapper.writeProxyCommand(commandLine, wrapper.getProxies().get(serviceId.getServerId()).getProxyInfo());
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
    public List<Candidate> onTab(long argsLength, String lastWord,ParsedLine parsedLine, String[] args) {
        List<Candidate> strings = new ArrayList<>();
        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("screen")) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("server")  || args[1].equalsIgnoreCase("-s")) {
                        strings.addAll(CloudNet.getInstance().getServers().values().stream().map(minecraftServer -> new Candidate(minecraftServer.getName(), minecraftServer.getName(), minecraftServer.getGroup().getName(), "A simple minecraft server", null, null, true)).collect(
                            Collectors.toList()));
                        return strings;
                    }
                    if (args[1].equalsIgnoreCase("proxy")  || args[1].equalsIgnoreCase("-p")) {
                        strings.addAll(CloudNet.getInstance().getProxys().values().stream().map(proxyServer -> new Candidate(proxyServer.getName(), proxyServer.getName(), proxyServer.getProcessMeta().getProxyGroupName(), "A simple proxy", null, null, true)).collect(
                            Collectors.toList()));
                        return strings;

                    }
                }
                strings.add(new Candidate("write", "write", null, "Write a command into the open screen", null, null, true));
                strings.add(new Candidate("server", "server", "screen-server", "Open a server screen", null, null,true));
                strings.add(new Candidate("-s", "-s", "screen-server", "Open a server screen", null, null,true));
                strings.add(new Candidate("proxy", "proxy", "screen-proxy", "Open a proxy screen", null, null,true));
                strings.add(new Candidate("-p", "-p", "screen-proxy", "Open a proxy screen", null, null,true));
                strings.add(new Candidate("leave", "leave", null, "Close/leave the current screen", null, null,true));
                return strings;
            }

        }
        return strings;
    }
}
