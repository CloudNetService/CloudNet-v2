package eu.cloudnetservice.cloudnet.v2.lib;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.InetAddress;

public class ConnectableAddress {

    public static final Type TYPE = TypeToken.get(ConnectableAddress.class).getType();
    private final InetAddress hostName;
    private final int port;

    public ConnectableAddress(InetAddress address, int port) {
        this.hostName = address;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getHostName() {
        return hostName;
    }
}
