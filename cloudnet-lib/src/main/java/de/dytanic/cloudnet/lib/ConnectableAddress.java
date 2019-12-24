package de.dytanic.cloudnet.lib;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Tareko on 07.06.2017.
 */
public class ConnectableAddress {

    public static final Type TYPE = TypeToken.get(ConnectableAddress.class).getType();
    private String hostName;
    private int port;

    public ConnectableAddress(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }
}
