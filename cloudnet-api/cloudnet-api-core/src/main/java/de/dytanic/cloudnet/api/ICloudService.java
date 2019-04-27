package de.dytanic.cloudnet.api;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 10.01.2018.
 */
public interface ICloudService {

    CloudPlayer getCachedPlayer(UUID uniqueId);

    CloudPlayer getCachedPlayer(String name);

    boolean isProxyInstance();

    Map<String, ServerInfo> getServers();

}