package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Created by Tareko on 01.06.2017.
 */
@AllArgsConstructor
@Getter
public class SimpleServerGroup
        implements Nameable {

    private String name;

    private boolean kickedForceFallback;

    private int joinPower;

    private int memory;

    private ServerGroupMode mode;

    private boolean maintenance;

    private int percentForNewServerAutomatically;

    private Map<String, Object> settings;

    private AdvancedServerConfig advancedServerConfig;

}
