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

package eu.cloudnetservice.cloudnet.v2.master.database;

import eu.cloudnetservice.cloudnet.v2.database.DatabaseManager;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;

public class DatabaseBasicHandlers {

    private final StatisticManager statisticManager;

    private final PlayerDatabase playerDatabase;

    private final CommandDispatcherDatabase commandDispatcherDatabase;

    private final NameToUUIDDatabase nameToUUIDDatabase;

    private final WrapperSessionDatabase wrapperSessionDatabase;

    private final UpdateConfigurationDatabase updateConfigurationDatabase;

    public DatabaseBasicHandlers(DatabaseManager databaseManager) {
        Database config = databaseManager.getDatabase("cloud_internal_cfg");

        playerDatabase = new PlayerDatabase(databaseManager.getDatabase("cloudnet_internal_players"));
        nameToUUIDDatabase = new NameToUUIDDatabase(databaseManager.getDatabase("cloud_internal_nameanduuid_dispatcher"));

        statisticManager = new StatisticManager(config);
        commandDispatcherDatabase = new CommandDispatcherDatabase(config);
        wrapperSessionDatabase = new WrapperSessionDatabase(databaseManager.getDatabase("cloudnet_internal_wrapper_session"));
        updateConfigurationDatabase = new UpdateConfigurationDatabase(config);

        nameToUUIDDatabase.handleUpdate(updateConfigurationDatabase);

        config.save();
    }

    public StatisticManager getStatisticManager() {
        return statisticManager;
    }

    public PlayerDatabase getPlayerDatabase() {
        return playerDatabase;
    }

    public CommandDispatcherDatabase getCommandDispatcherDatabase() {
        return commandDispatcherDatabase;
    }

    public NameToUUIDDatabase getNameToUUIDDatabase() {
        return nameToUUIDDatabase;
    }

    public WrapperSessionDatabase getWrapperSessionDatabase() {
        return wrapperSessionDatabase;
    }

    public UpdateConfigurationDatabase getUpdateConfigurationDatabase() {
        return updateConfigurationDatabase;
    }
}
