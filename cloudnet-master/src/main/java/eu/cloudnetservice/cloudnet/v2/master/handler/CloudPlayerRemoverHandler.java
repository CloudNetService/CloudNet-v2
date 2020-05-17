package eu.cloudnetservice.cloudnet.v2.master.handler;

import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

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
                                       .flatMap(maps -> maps.keySet().stream())
                                       .collect(Collectors.toSet());

        CloudNet.getInstance().getNetworkManager().getOnlinePlayers()
                .keySet()
                .removeIf(uuid -> !collection.contains(uuid));
    }

    @Override
    public int getTicks() {
        return 10;
    }
}
