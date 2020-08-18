package eu.cloudnetservice.cloudnet.v2.lib;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.InetAddress;

/**
 * Class specifying a Adress & Port Combination
 * for Wrapper & Master Connections
 */
public class ConnectableAddress {

    public static final Type TYPE = TypeToken.get(ConnectableAddress.class).getType();
    private final InetAddress hostName;
    private final int port;

    public ConnectableAddress(InetAddress adress, int port) {
        this.hostName = adress;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getHostName() {
        return hostName;
    }
}
