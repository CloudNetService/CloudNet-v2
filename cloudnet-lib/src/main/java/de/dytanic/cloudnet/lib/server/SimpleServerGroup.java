package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Tareko on 01.06.2017.
 */
public class SimpleServerGroup implements Nameable {

    private final String name;
    private final boolean kickedForceFallback;
    private final int joinPower;
    private final int memory;
    private final ServerGroupMode mode;
    private final boolean maintenance;
    private final int percentForNewServerAutomatically;
    private final Map<String, Object> settings;
    private final AdvancedServerConfig advancedServerConfig;

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
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (kickedForceFallback ? 1 : 0);
        result = 31 * result + joinPower;
        result = 31 * result + memory;
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        result = 31 * result + (maintenance ? 1 : 0);
        result = 31 * result + percentForNewServerAutomatically;
        result = 31 * result + (settings != null ? settings.hashCode() : 0);
        result = 31 * result + (advancedServerConfig != null ? advancedServerConfig.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SimpleServerGroup that = (SimpleServerGroup) o;

        if (kickedForceFallback != that.kickedForceFallback) {
            return false;
        }
        if (joinPower != that.joinPower) {
            return false;
        }
        if (memory != that.memory) {
            return false;
        }
        if (maintenance != that.maintenance) {
            return false;
        }
        if (percentForNewServerAutomatically != that.percentForNewServerAutomatically) {
            return false;
        }
        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (mode != that.mode) {
            return false;
        }
        if (!Objects.equals(settings, that.settings)) {
            return false;
        }
        return Objects.equals(advancedServerConfig, that.advancedServerConfig);
    }

    @Override
    public String toString() {
        return "SimpleServerGroup{" +
            "name='" + name + '\'' +
            ", kickedForceFallback=" + kickedForceFallback +
            ", joinPower=" + joinPower +
            ", memory=" + memory +
            ", mode=" + mode +
            ", maintenance=" + maintenance +
            ", percentForNewServerAutomatically=" + percentForNewServerAutomatically +
            ", settings=" + settings +
            ", advancedServerConfig=" + advancedServerConfig +
            '}';
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
