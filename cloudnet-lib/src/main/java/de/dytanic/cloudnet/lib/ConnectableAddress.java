package de.dytanic.cloudnet.lib;

/**
 * Created by Tareko on 07.06.2017.
 */
public class ConnectableAddress {

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
