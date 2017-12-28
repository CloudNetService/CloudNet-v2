/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.util;

import de.dytanic.cloudnet.lib.map.WrappedMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 07.10.2017.
 */
@Getter
@AllArgsConstructor
public class PathProvider {

    private String path;

    private WrappedMap pathParameters;

}