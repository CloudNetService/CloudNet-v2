package de.dytanic.cloudnet.lib.player;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Objects;

/**
 *
 */
public class CloudPlayer extends OfflinePlayer {
    public static final Type TYPE = new TypeToken<CloudPlayer>() {}.getType();
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
        this.playerExecutor = new PlayerExecutor();
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

    @Override
    public int hashCode() {
        int result = playerConnection != null ? playerConnection.hashCode() : 0;
        result = 31 * result + (proxy != null ? proxy.hashCode() : 0);
        result = 31 * result + (server != null ? server.hashCode() : 0);
        result = 31 * result + (loginTimeStamp != null ? loginTimeStamp.hashCode() : 0);
        result = 31 * result + (playerExecutor != null ? playerExecutor.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudPlayer)) {
            return false;
        }
        final CloudPlayer that = (CloudPlayer) o;
        return Objects.equals(playerConnection, that.playerConnection) && Objects.equals(proxy, that.proxy) && Objects.equals(server,
                                                                                                                              that.server) && Objects
            .equals(loginTimeStamp, that.loginTimeStamp) && Objects.equals(playerExecutor, that.playerExecutor);
    }

    @Override
    public String toString() {
        return "CloudPlayer{" + "playerConnection=" + playerConnection + ", proxy='" + proxy + '\'' + ", server='" + server + '\'' + ", loginTimeStamp=" + loginTimeStamp + ", playerExecutor=" + playerExecutor + "} " + super
            .toString();
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

    public PlayerExecutor getPlayerExecutor() {
        return playerExecutor;
    }

    public void setPlayerExecutor(PlayerExecutor playerExecutor) {
        this.playerExecutor = playerExecutor;
    }
}
