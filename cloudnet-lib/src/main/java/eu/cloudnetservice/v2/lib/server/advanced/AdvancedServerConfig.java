package eu.cloudnetservice.v2.lib.server.advanced;

/**
 * Created by Tareko on 16.09.2017.
 */
public class AdvancedServerConfig {

    private final boolean notifyPlayerUpdatesFromNoCurrentPlayer;

    private final boolean notifyProxyUpdates;

    private final boolean notifyServerUpdates;

    private final boolean disableAutoSavingForWorlds;

    public AdvancedServerConfig(boolean notifyPlayerUpdatesFromNoCurrentPlayer,
                                boolean notifyProxyUpdates,
                                boolean notifyServerUpdates,
                                boolean disableAutoSavingForWorlds) {
        this.notifyPlayerUpdatesFromNoCurrentPlayer = notifyPlayerUpdatesFromNoCurrentPlayer;
        this.notifyProxyUpdates = notifyProxyUpdates;
        this.notifyServerUpdates = notifyServerUpdates;
        this.disableAutoSavingForWorlds = disableAutoSavingForWorlds;
    }

    public boolean isDisableAutoSavingForWorlds() {
        return disableAutoSavingForWorlds;
    }

    public boolean isNotifyPlayerUpdatesFromNoCurrentPlayer() {
        return notifyPlayerUpdatesFromNoCurrentPlayer;
    }

    public boolean isNotifyProxyUpdates() {
        return notifyProxyUpdates;
    }

    public boolean isNotifyServerUpdates() {
        return notifyServerUpdates;
    }
}
