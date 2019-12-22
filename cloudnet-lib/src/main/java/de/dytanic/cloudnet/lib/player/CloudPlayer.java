package de.dytanic.cloudnet.lib.player;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Objects;

/**
 *
 */
public class CloudPlayer extends OfflinePlayer {
    public static final Type TYPE = TypeToken.get(CloudPlayer.class).getType();


    private PlayerConnection playerConnection;
    private String proxy;
    private String server;
    private Timestamp loginTimeStamp;
    private PlayerExecutor playerExecutor;

    public CloudPlayer(OfflinePlayer player, PlayerConnection onlineConnection, String proxy) {
        super(player.getUniqueId(),
              player.getName(),
              player.getMetaData(),
              player.getLastLogin(),
              player.getFirstLogin(),
              player.getLastPlayerConnection(),
              player.getPermissionEntity());

        this.playerConnection = onlineConnection;
        this.proxy = proxy;
        this.server = null;
        this.loginTimeStamp = new Timestamp(System.currentTimeMillis());
    }

    public static OfflinePlayer newOfflinePlayer(OfflinePlayer cloudPlayer) {
        return new OfflinePlayer(cloudPlayer.getUniqueId(),
                                 cloudPlayer.getName(),
                                 cloudPlayer.getMetaData(),
                                 cloudPlayer.getLastLogin(),
                                 cloudPlayer.getFirstLogin(),
                                 cloudPlayer.getLastPlayerConnection(),
                                 cloudPlayer.getPermissionEntity());
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public PlayerConnection getPlayerConnection() {
        return playerConnection;
    }

    public void setPlayerConnection(PlayerConnection playerConnection) {
        this.playerConnection = playerConnection;
    }

    public Timestamp getLoginTimeStamp() {
        return loginTimeStamp;
    }

    public void setLoginTimeStamp(Timestamp loginTimeStamp) {
        this.loginTimeStamp = loginTimeStamp;
    }

    @Override
    public String toString() {
        return "CloudPlayer{" +
            "playerConnection=" + playerConnection +
            ", proxy='" + proxy + '\'' +
            ", server='" + server + '\'' +
        ", loginTimeStamp=" + loginTimeStamp +
            '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CloudPlayer that = (CloudPlayer) o;
        return Objects.equals(getPlayerConnection(), that.getPlayerConnection()) &&
            Objects.equals(getProxy(), that.getProxy()) &&
            Objects.equals(getServer(), that.getServer()) &&
            Objects.equals(getLoginTimeStamp(), that.getLoginTimeStamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerConnection(), getProxy(), getServer(), getLoginTimeStamp());
    }

    public PlayerExecutor getPlayerExecutor() {
        return playerExecutor;
    }

    public void setPlayerExecutor(final PlayerExecutor playerExecutor) {
        this.playerExecutor = playerExecutor;
    }
}
