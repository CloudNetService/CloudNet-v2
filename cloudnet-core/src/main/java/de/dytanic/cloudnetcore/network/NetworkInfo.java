/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 27.05.2017.
 */
@AllArgsConstructor
@Getter
public class NetworkInfo {

    private String serverId;
    private String hostName;
    private int port;

}
