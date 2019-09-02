package de.dytanic.cloudnet.lib.player;

import com.google.gson.reflect.TypeToken;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.lang.reflect.Type;
import java.sql.Timestamp;

/**
 *
 */
@ToString
@EqualsAndHashCode
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
