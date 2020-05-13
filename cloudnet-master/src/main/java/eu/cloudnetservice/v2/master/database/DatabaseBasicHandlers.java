package eu.cloudnetservice.v2.master.database;

import de.dytanic.cloudnet.database.DatabaseManager;
import de.dytanic.cloudnet.lib.database.Database;

/**
 * Created by Tareko on 24.07.2017.
 */
public class DatabaseBasicHandlers {

    private StatisticManager statisticManager;

    private PlayerDatabase playerDatabase;

    private CommandDispatcherDatabase commandDispatcherDatabase;

    private NameToUUIDDatabase nameToUUIDDatabase;

    private WrapperSessionDatabase wrapperSessionDatabase;

    private UpdateConfigurationDatabase updateConfigurationDatabase;

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
