/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.advanced;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 16.09.2017.
 */
@Getter
@AllArgsConstructor
public class AdvancedServerConfig {

    private boolean notifyPlayerUpdatesFromNoCurrentPlayer;

    private boolean notifyProxyUpdates;

    private boolean notifyServerUpdates;

    private boolean disableAutoSavingForWorlds;

}