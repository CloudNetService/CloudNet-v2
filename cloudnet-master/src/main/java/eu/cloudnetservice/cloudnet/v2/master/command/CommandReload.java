package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import org.jline.reader.ParsedLine;

public final class CommandReload extends Command {

    public CommandReload() {
        super("reload", "cloudnet.command.reload", "rl");

        description = "Reloads the config and modules";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("all")) {
                sender.sendMessage("[RELOAD] Trying to reload CloudNet...");
                try {
                    CloudNet.getInstance().reload();
                    sender.sendMessage("[RELOAD] Reloading was completed successfully!");
                } catch (Exception e) {
                    sender.sendMessage("[RELOAD] Failed to reload CloudNet");
                    e.printStackTrace();
                }
            }
            if (args[0].equalsIgnoreCase("config")) {
                sender.sendMessage("[RELOAD] Trying to reload config");
                reloadConfig();
                sender.sendMessage("[RELOAD] Reloading was completed successfully");
            }
            if (args[0].equalsIgnoreCase("wrapper")) {
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
            CloudNet.getLogger().info(String.format("Loading server group: %s%n", serverGroup.getName()));
            CloudNet.getInstance().setupGroup(serverGroup);
        });

        CloudNet.getInstance().getProxyGroups().putAll(CloudNet.getInstance().getConfig().getProxyGroups());
        CloudNet.getInstance().getProxyGroups().forEach((name, proxyGroup) -> {
            CloudNet.getLogger().info(String.format("Loading proxy group: %s%n", proxyGroup.getName()));
            CloudNet.getInstance().setupProxy(proxyGroup);
        });

        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll();
        CloudNet.getInstance().getWrappers().values().forEach(Wrapper::updateWrapper);
    }
}
