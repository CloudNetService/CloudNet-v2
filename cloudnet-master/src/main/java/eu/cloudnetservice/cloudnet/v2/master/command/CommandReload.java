package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;

public final class CommandReload extends Command implements TabCompletable {

    public CommandReload() {
        super("reload", "cloudnet.command.reload", "rl");

        description = "Reloads the config and modules";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        if (parsedLine.words().size() == 2) {
            String commandArgument = parsedLine.words().get(1);
            if (commandArgument.equalsIgnoreCase("all")) {
                sender.sendMessage("§e[RELOAD] Trying to reload CloudNet...");
                try {
                    CloudNet.getInstance().reload();
                    sender.sendMessage("§a[RELOAD] Reloading was completed successfully!");
                } catch (Exception e) {
                    sender.sendMessage("§c[RELOAD] Failed to reload CloudNet");
                    e.printStackTrace();
                }
            }
            if (commandArgument.equalsIgnoreCase("config")) {
                sender.sendMessage("§e[RELOAD] Trying to reload config");
                reloadConfig();
                sender.sendMessage("§a[RELOAD] Reloading was completed successfully");
            }
            if (commandArgument.equalsIgnoreCase("wrapper")) {
                for (Wrapper wrapper : CloudNet.getInstance().getWrappers().values()) {
                    if (wrapper.getChannel() != null) {
                        wrapper.writeCommand("reload");
                    }
                }
            }
        } else {
            sender.sendMessage("reload ALL | Loads all groups as well as modules, permissions, etc.",
                               "reload CONFIG | Reload the configuration file, and its server groups etc.",
                               "reload WRAPPER | Dispatched on all wrappers the command \"reload\"");
        }
    }

    public static void reloadConfig() {
        try {
            CloudNet.getInstance().getConfig().load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CloudNet.getInstance().getServerGroups().clear();
        CloudNet.getInstance().getProxyGroups().clear();
        CloudNet.getInstance().getUsers().clear();
        CloudNet.getInstance().getUsers().addAll(CloudNet.getInstance().getConfig().getUsers());

        CloudNet.getInstance().getServerGroups().putAll(CloudNet.getInstance().getConfig().getServerGroups());
        CloudNet.getInstance().getServerGroups().forEach((name, serverGroup) -> {
            CloudNet.getLogger().info(String.format("Loading server group: %s", serverGroup.getName()));
            CloudNet.getInstance().setupGroup(serverGroup);
        });

        CloudNet.getInstance().getProxyGroups().putAll(CloudNet.getInstance().getConfig().getProxyGroups());
        CloudNet.getInstance().getProxyGroups().forEach((name, proxyGroup) -> {
            CloudNet.getLogger().info(String.format("Loading proxy group: %s", proxyGroup.getName()));
            CloudNet.getInstance().setupProxy(proxyGroup);
        });

        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll();
        CloudNet.getInstance().getWrappers().values().forEach(Wrapper::updateWrapper);
    }

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> strings = new ArrayList<>();
        if (parsedLine.words().size() >= 1) {
            String commandArgument = parsedLine.words().get(0);
            if (commandArgument.equalsIgnoreCase("reload") || commandArgument.equalsIgnoreCase("rl")) {
                strings.add(new Candidate("ALL", "ALL", null, "Loads all groups as well as modules, permissions, etc.", null ,null, true));
                strings.add(new Candidate("CONFIG", "CONFIG", null, "Reload the configuration file, and its server groups etc.", null ,null, true));
                strings.add(new Candidate("WRAPPER", "WRAPPER", null, "Dispatched on all wrappers the command \"reload\"", null ,null, true));
            }
        }
        return strings;
    }
}
