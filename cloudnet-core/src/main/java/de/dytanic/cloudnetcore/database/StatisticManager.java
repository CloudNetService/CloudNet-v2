/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.database;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

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
        if (!database.containsDoc(NAME)) {
            Document document = new DatabaseDocument(NAME);
            database.insert(document);
        }

        if (CloudNet.getInstance().getOptionSet().has("disable-statistics")) {
            statistic = false;
        }
    }

    public static StatisticManager getInstance() {
        return instance;
    }

    public Document getStatistics() {
        return database.getDocument(NAME);
    }

    public void addPlayerLogin() {
        if (!statistic) {
            return;
        }
        try {
            Document document = database.getDocument(NAME);
            if (!document.contains("playerLogin")) {
                document.append("playerLogin", 0L);
            }

            document.append("playerLogin", document.getLong("playerLogin") + 1L);
        } catch (Exception ex) {

        }
    }

    public void addStartedProxys() {
        if (!statistic) {
            return;
        }
        Document document = database.getDocument(NAME);
        if (!document.contains("startedProxys")) {
            document.append("startedProxys", 0);
        }

        document.append("startedProxys", document.getInt("startedProxys") + 1);
    }

    public void addStartedServers() {
        if (!statistic) {
            return;
        }
        Document document = database.getDocument(NAME);
        if (!document.contains("startedServers")) {
            document.append("startedServers", 0L);
        }

        document.append("startedServers", document.getLong("startedServers") + 1L);
    }

    public void addStartup() {
        if (!statistic) {
            return;
        }
        Document document = database.getDocument(NAME);
        if (!document.contains("cloudStartup")) {
            document.append("cloudStartup", 0L);
        }

        document.append("cloudStartup", document.getLong("cloudStartup") + 1L);
    }

    public void wrapperConnections() {
        if (!statistic) {
            return;
        }
        Document document = database.getDocument(NAME);
        if (!document.contains("wrapperConnections")) {
            document.append("wrapperConnections", 0);
        }

        document.append("wrapperConnections", document.getLong("wrapperConnections") + 1L);
    }

    public void playerCommandExecutions() {
        if (!statistic) {
            return;
        }
        Document document = database.getDocument(NAME);
        if (!document.contains("playerCommandExecutions")) {
            document.append("playerCommandExecutions", 0L);
        }

        document.append("playerCommandExecutions", document.getLong("playerCommandExecutions") + 1L);
    }

    public void highestServerOnlineCount(int value) {
        if (!statistic) {
            return;
        }
        Document document = database.getDocument(NAME);
        if (!document.contains("highestServerOnlineCount")) {
            document.append("highestServerOnlineCount", 0);
        }

        if (value > document.getInt("highestServerOnlineCount")) {
            document.append("highestServerOnlineCount", value);
        }
    }

    public void highestPlayerOnlineCount(int value) {
        if (!statistic) {
            return;
        }
        Document document = database.getDocument(NAME);
        if (!document.contains("highestPlayerOnline")) {
            document.append("highestPlayerOnline", 0);
        }

        if (value > document.getInt("highestPlayerOnline")) {
            document.append("highestPlayerOnline", value);
        }
    }

    public void cloudOnlineTime(long activeNow) {
        if (!statistic) {
            return;
        }
        Document document = database.getDocument(NAME);
        if (!document.contains("cloudOnlineTime")) {
            document.append("cloudOnlineTime", 0);
        }
        long append = System.currentTimeMillis() - activeNow;
        document.append("cloudOnlineTime", document.getLong("cloudOnlineTime") + append);
    }
}
