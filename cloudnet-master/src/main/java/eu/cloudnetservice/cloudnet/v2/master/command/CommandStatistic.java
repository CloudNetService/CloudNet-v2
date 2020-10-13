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
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.database.StatisticManager;

import java.util.concurrent.TimeUnit;

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
