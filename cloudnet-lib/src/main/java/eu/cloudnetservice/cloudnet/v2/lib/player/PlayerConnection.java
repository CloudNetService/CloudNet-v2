package eu.cloudnetservice.cloudnet.v2.lib.player;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Created by Tareko on 27.07.2017.
 */
public class PlayerConnection {

    public static final Type TYPE = TypeToken.get(PlayerConnection.class).getType();

    private final UUID uniqueId;
    private final String name;
    private final int version;
    private final String host;
    private final int port;
    private final boolean onlineMode;
    private final boolean legacy;

    public PlayerConnection(UUID uniqueId, String name, int version, String host, int port, boolean onlineMode, boolean legacy) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.version = version;
        this.host = host;
        this.port = port;
        this.onlineMode = onlineMode;
        this.legacy = legacy;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public int getVersion() {
        return version;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public boolean isLegacy() {
        return legacy;
    }
}
