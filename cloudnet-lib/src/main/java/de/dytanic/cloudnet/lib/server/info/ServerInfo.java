package de.dytanic.cloudnet.lib.server.info;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import java.lang.reflect.Type;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Tareko on 24.05.2017.
 */
@Getter
@AllArgsConstructor
@ToString
public class ServerInfo {

    public static final Type TYPE = new TypeToken<ServerInfo>() {
    }.getType();

    private ServiceId serviceId;

    private String host;
    private int port;
    private boolean online;
    private List<String> players;
    private int memory;
    private String motd;
    private int onlineCount;
    private int maxPlayers;
    private ServerState serverState;
    private ServerConfig serverConfig;
    private Template template;

    public boolean isIngame()
    {

        if (serverState == null) serverState = ServerState.LOBBY;

        if (motd == null) motd = "null";

        return serverState == ServerState.INGAME || (motd.equalsIgnoreCase("INGAME") || motd.equalsIgnoreCase("RUNNING"));
    }

    public SimpleServerInfo toSimple()
    {
        return new SimpleServerInfo(serviceId, host, port, onlineCount, maxPlayers);
    }

}
