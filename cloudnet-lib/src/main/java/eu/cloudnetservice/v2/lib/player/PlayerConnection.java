package eu.cloudnetservice.v2.lib.player;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Created by Tareko on 27.07.2017.
 */
public class PlayerConnection {

    public static final Type TYPE = TypeToken.get(PlayerConnection.class).getType();

    private UUID uniqueId;
    private String name;
    private int version;
    private String host;
    private int port;
    private boolean onlineMode;
    private boolean legacy;

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
