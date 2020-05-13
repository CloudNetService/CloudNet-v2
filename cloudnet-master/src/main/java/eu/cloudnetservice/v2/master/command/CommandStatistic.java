package eu.cloudnetservice.v2.master.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.database.StatisticManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 21.08.2017.
 */
public final class CommandStatistic extends Command {

    public CommandStatistic() {
        super("statistic", "cloudnet.command.statistic");

        description = "Shows a list of all recorded statistics of this CloudNet instance!";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        DatabaseDocument document = StatisticManager.getInstance().getStatistics();
        sender.sendMessage("CloudNet2 Statistics:",
                           " ",
                           "CloudStartups: " + StatisticManager.getInstance().getStatistics().getInt("cloudStartup"),
                           "Cloud online time: " + TimeUnit.MILLISECONDS.toMinutes(document.getInt("cloudOnlineTime")) + "min",
                           "Wrapper connections: " + document.getInt("wrapperConnections"),
                           "Highest server online count: " + document.getInt("highestServerOnlineCount"),
                           "Started servers: " + document.getLong("startedServers"),
                           " ",
                           "Player Statistics:",
                           " ",
                           "Registered: " + CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getDatabase().size(),
                           "Highest online count: " + document.getInt("highestPlayerOnline"),
                           "Logins: " + document.getInt("playerLogin"),
                           "Command executions: " + document.getInt("playerCommandExecutions"),
                           " ");
    }
}
