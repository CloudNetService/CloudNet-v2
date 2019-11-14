package de.dytanic.cloudnetcore.handler;

import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 20.11.2017.
 */
public class CloudPlayerRemoverHandler implements ICloudHandler {

    @Override
    public void onHandle(CloudNet cloudNet) {
        Set<UUID> collection = CloudNet.getInstance().getProxys().values().stream()
                                       .map(ProxyServer::getProxyInfo)
                                       .map(ProxyInfo::getPlayers)
                                       .flatMap(multiValues -> multiValues.stream().map(MultiValue::getFirst))
                                       .collect(Collectors.toSet());

        CloudNet.getInstance().getNetworkManager().getOnlinePlayers()
                .keySet()
                .removeIf(uuid ->
                              !collection.contains(uuid));
    }

    @Override
    public int getTicks() {
        return 10;
    }
}
