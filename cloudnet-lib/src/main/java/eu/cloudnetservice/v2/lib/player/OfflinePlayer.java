package eu.cloudnetservice.v2.lib.player;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.v2.lib.interfaces.Nameable;
import eu.cloudnetservice.v2.lib.utility.document.Document;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.UUID;

public class OfflinePlayer implements Nameable, Serializable {

    public static final Type TYPE = TypeToken.get(OfflinePlayer.class).getType();

    protected UUID uniqueId;

    protected String name;

    protected Document metaData;

    protected Long lastLogin;

    protected Long firstLogin;

    protected PlayerConnection lastPlayerConnection;

    public OfflinePlayer(UUID uniqueId,
                         String name,
                         Document metaData,
                         Long lastLogin,
                         Long firstLogin,
                         PlayerConnection lastPlayerConnection) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.metaData = metaData;
        this.lastLogin = lastLogin;
        this.firstLogin = firstLogin;
        this.lastPlayerConnection = lastPlayerConnection;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Document getMetaData() {
        return metaData;
    }

    public void setMetaData(Document metaData) {
        this.metaData = metaData;
    }

    public Long getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Long firstLogin) {
        this.firstLogin = firstLogin;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public PlayerConnection getLastPlayerConnection() {
        return lastPlayerConnection;
    }

    public void setLastPlayerConnection(PlayerConnection lastPlayerConnection) {
        this.lastPlayerConnection = lastPlayerConnection;
    }
}
