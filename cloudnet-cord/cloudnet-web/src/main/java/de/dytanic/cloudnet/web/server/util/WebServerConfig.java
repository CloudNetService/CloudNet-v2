/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 16.09.2017.
 */
@Getter
@AllArgsConstructor
public class WebServerConfig {

    private boolean enabled;

    private String address;

    private int port;

}