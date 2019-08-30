package de.dytanic.cloudnet.lib.server.priority;

/**
 * Created by Tareko on 18.07.2017.
 */
public class PriorityConfig {

    private int onlineServers;
    private int onlineCount;

    public PriorityConfig(int onlineServers, int onlineCount) {
        this.onlineServers = onlineServers;
        this.onlineCount = onlineCount;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public int getOnlineServers() {
        return onlineServers;
    }
}