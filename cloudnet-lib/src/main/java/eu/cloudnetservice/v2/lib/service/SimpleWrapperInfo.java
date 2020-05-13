package eu.cloudnetservice.v2.lib.service;

import eu.cloudnetservice.v2.lib.interfaces.Nameable;

/**
 * Created by Tareko on 13.09.2017.
 */
public class SimpleWrapperInfo implements Nameable {

    private final String name;

    private final String hostName;

    public SimpleWrapperInfo(String name, String hostName) {
        this.name = name;
        this.hostName = hostName;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getHostName() {
        return hostName;
    }
}
