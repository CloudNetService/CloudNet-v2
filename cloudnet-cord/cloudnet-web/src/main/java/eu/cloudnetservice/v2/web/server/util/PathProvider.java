package eu.cloudnetservice.v2.web.server.util;

import eu.cloudnetservice.v2.lib.map.WrappedMap;

/**
 * Data class that holds information for a dynamic path like in Spring
 */
public class PathProvider {

    /**
     * The path where a request has been sent to.
     */
    private String path;

    /**
     * The parameters of a request to {@code path}.
     */
    private WrappedMap pathParameters;

    public PathProvider(String path, WrappedMap pathParameters) {
        this.path = path;
        this.pathParameters = pathParameters;
    }

    public String getPath() {
        return path;
    }

    public WrappedMap getPathParameters() {
        return pathParameters;
    }
}
