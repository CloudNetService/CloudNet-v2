package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;

import java.util.Map;

/**
 * Created by Tareko on 01.06.2017.
 */
public class SimpleServerGroup implements Nameable {

    private String name;

    private boolean kickedForceFallback;

    private int joinPower;

    private int memory;

    private ServerGroupMode mode;

    private boolean maintenance;

    private int percentForNewServerAutomatically;

    private Map<String, Object> settings;

    private AdvancedServerConfig advancedServerConfig;

    public SimpleServerGroup(String name,
                             boolean kickedForceFallback,
                             int joinPower,
                             int memory,
                             ServerGroupMode mode,
                             boolean maintenance,
                             int percentForNewServerAutomatically,
                             Map<String, Object> settings,
                             AdvancedServerConfig advancedServerConfig) {
        this.name = name;
        this.kickedForceFallback = kickedForceFallback;
        this.joinPower = joinPower;
        this.memory = memory;
        this.mode = mode;
        this.maintenance = maintenance;
        this.percentForNewServerAutomatically = percentForNewServerAutomatically;
        this.settings = settings;
        this.advancedServerConfig = advancedServerConfig;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getMemory() {
        return memory;
    }

    public AdvancedServerConfig getAdvancedServerConfig() {
        return advancedServerConfig;
    }

    public int getJoinPower() {
        return joinPower;
    }

    public int getPercentForNewServerAutomatically() {
        return percentForNewServerAutomatically;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public ServerGroupMode getMode() {
        return mode;
    }

    public boolean isKickedForceFallback() {
        return kickedForceFallback;
    }

    public boolean isMaintenance() {
        return maintenance;
    }
}
