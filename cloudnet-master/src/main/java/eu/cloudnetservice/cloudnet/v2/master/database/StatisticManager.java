package eu.cloudnetservice.cloudnet.v2.master.database;

import eu.cloudnetservice.cloudnet.v2.database.DatabaseUsable;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

/**
 * Created by Tareko on 21.08.2017.
 */
public class StatisticManager extends DatabaseUsable {

    private static final String NAME = "statistics";
    private static StatisticManager instance;
    private boolean statistic = true;

    public StatisticManager(Database database) {
        super(database);
        instance = this;
        if (!database.contains(NAME)) {
            DatabaseDocument document = new DatabaseDocument(NAME);
            database.insert(document);
        }

        if (CloudNet.getInstance().getOptionSet().has("disable-statistics")) {
            statistic = false;
        }
    }

    public static StatisticManager getInstance() {
        return instance;
    }

    public DatabaseDocument getStatistics() {
        return database.getDocument(NAME);
    }

    public void addPlayerLogin() {
        if (!statistic) {
            return;
        }
        try {
            DatabaseDocument document = database.getDocument(NAME);
            if (!document.contains("playerLogin")) {
                document.append("playerLogin", 0L);
            }

            document.append("playerLogin", document.getLong("playerLogin") + 1L);
            database.insert(document);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addStartedProxys() {
        if (!statistic) {
            return;
        }
        DatabaseDocument document = database.getDocument(NAME);
        if (!document.contains("startedProxys")) {
            document.append("startedProxys", 0);
        }

        document.append("startedProxys", document.getInt("startedProxys") + 1);
        database.insert(document);
    }

    public void addStartedServers() {
        if (!statistic) {
            return;
        }
        DatabaseDocument document = database.getDocument(NAME);
        if (!document.contains("startedServers")) {
            document.append("startedServers", 0L);
        }

        document.append("startedServers", document.getLong("startedServers") + 1L);
        database.insert(document);
    }

    public void addStartup() {
        if (!statistic) {
            return;
        }
        DatabaseDocument document = database.getDocument(NAME);
        if (!document.contains("cloudStartup")) {
            document.append("cloudStartup", 0L);
        }

        document.append("cloudStartup", document.getLong("cloudStartup") + 1L);
        database.insert(document);
    }

    public void wrapperConnections() {
        if (!statistic) {
            return;
        }
        DatabaseDocument document = database.getDocument(NAME);
        if (!document.contains("wrapperConnections")) {
            document.append("wrapperConnections", 0);
        }

        document.append("wrapperConnections", document.getLong("wrapperConnections") + 1L);
        database.insert(document);
    }

    public void playerCommandExecutions() {
        if (!statistic) {
            return;
        }
        DatabaseDocument document = database.getDocument(NAME);
        if (!document.contains("playerCommandExecutions")) {
            document.append("playerCommandExecutions", 0L);
        }

        document.append("playerCommandExecutions", document.getLong("playerCommandExecutions") + 1L);
        database.insert(document);
    }

    public void highestServerOnlineCount(int value) {
        if (!statistic) {
            return;
        }
        DatabaseDocument document = database.getDocument(NAME);
        if (!document.contains("highestServerOnlineCount")) {
            document.append("highestServerOnlineCount", 0);
        }

        if (value > document.getInt("highestServerOnlineCount")) {
            document.append("highestServerOnlineCount", value);
        }
        database.insert(document);
    }

    public void highestPlayerOnlineCount(int value) {
        if (!statistic) {
            return;
        }
        DatabaseDocument document = database.getDocument(NAME);
        if (!document.contains("highestPlayerOnline")) {
            document.append("highestPlayerOnline", 0);
        }

        if (value > document.getInt("highestPlayerOnline")) {
            document.append("highestPlayerOnline", value);
        }
        database.insert(document);
    }

    public void cloudOnlineTime(long activeNow) {
        if (!statistic) {
            return;
        }
        DatabaseDocument document = database.getDocument(NAME);
        if (!document.contains("cloudOnlineTime")) {
            document.append("cloudOnlineTime", 0);
        }
        long append = System.currentTimeMillis() - activeNow;
        document.append("cloudOnlineTime", document.getLong("cloudOnlineTime") + append);
        database.insert(document);
    }
}
