package eu.cloudnetservice.cloudnet.v2.master.network;


import java.net.InetAddress;

public class NetworkInfo {

    private final String serverId;
    private final InetAddress hostName;
    private final int port;

    public NetworkInfo(String serverId, InetAddress hostName, int port) {
        this.serverId = serverId;
        this.hostName = hostName;
        this.port = port;
    }

    public InetAddress getHostName() {
        return hostName;
    }

    public String getServerId() {
        return serverId;
    }

    public int getPort() {
        return port;
    }
}
