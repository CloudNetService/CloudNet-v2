package eu.cloudnetservice.v2.bridge.internal.command.bukkit;

import com.google.common.collect.ImmutableList;
import eu.cloudnetservice.v2.api.CloudAPI;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 23.08.2017.
 */
public final class CommandCloudServer implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        CloudAPI.getInstance().getLogger().finest(String.format("%s executed %s (label = %s) with arguments %s%n",
                                                                commandSender,
                                                                command,
                                                                label,
                                                                Arrays.toString(args)));


        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("debug")) {
                return debug(commandSender);
            }
        }

        help(commandSender);

        return false;
    }

    private static boolean playerGuard(CommandSender sender, Function<Player, Boolean> method) {
        if (sender instanceof Player) {
            return method.apply((Player) sender);
        } else {
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "This command can only be called by a player!");
            return false;
        }
    }

    private boolean debug(CommandSender commandSender) {
        CloudAPI.getInstance().setDebug(!CloudAPI.getInstance().isDebug());

        final LoggerContext context = (LoggerContext) LogManager.getContext(false);
        final Configuration configuration = context.getConfiguration();
        final LoggerConfig rootLoggerConfig = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);

        if (CloudAPI.getInstance().isDebug()) {
            rootLoggerConfig.setLevel(Level.ALL);
            commandSender.sendMessage("§aDebug output for server has been enabled.");
        } else {
            rootLoggerConfig.setLevel(Level.INFO);
            commandSender.sendMessage("§cDebug output for server has been disabled.");
        }
        context.updateLoggers(configuration);
        return false;
    }

    private void help(CommandSender commandSender) {
        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs copyTo <targetGroup>");
        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs debug");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return ImmutableList.of(
                "debug")
                                .stream()
                                .filter(s -> s.startsWith(args[args.length - 1]))
                                .sorted()
                                .collect(Collectors.toList());
        }
        return ImmutableList.of();
    }
}
