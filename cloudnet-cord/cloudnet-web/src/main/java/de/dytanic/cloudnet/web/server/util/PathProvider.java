/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.util;

import de.dytanic.cloudnet.lib.map.WrappedMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data class that holds information for a dynamic path like in Spring
 */
@Getter
@AllArgsConstructor
public class PathProvider {

    /**
     * The path where a request has been sent to.
     */
    private String path;

    /**
     * The parameters of a request to {@code path}.
     */
    private WrappedMap pathParameters;

}
