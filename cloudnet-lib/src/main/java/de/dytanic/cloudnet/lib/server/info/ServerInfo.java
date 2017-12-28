package de.dytanic.cloudnet.lib.server.info;

import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.service.ServiceId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by Tareko on 24.05.2017.
 */
@Getter
@AllArgsConstructor
public class ServerInfo {

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

        if(serverState == null) serverState = ServerState.LOBBY;

        if(motd == null) motd = "null";

        return serverState == ServerState.INGAME || (motd.equalsIgnoreCase("INGAME") || motd.equalsIgnoreCase("RUNNING"));
    }

    public SimpleServerInfo toSimple()
    {
        return new SimpleServerInfo(serviceId, host, port, onlineCount, maxPlayers);
    }

}