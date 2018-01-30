/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 22.08.2017.
 */
@Getter
@AllArgsConstructor
public class CustomServerConfig {

    private String serverId;

    private int memory;

    private String group, wrapper;

    private boolean onlineMode;

}