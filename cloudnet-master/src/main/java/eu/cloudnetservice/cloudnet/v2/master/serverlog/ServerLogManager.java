package eu.cloudnetservice.cloudnet.v2.master.serverlog;

import eu.cloudnetservice.cloudnet.v2.lib.MultiValue;
import eu.cloudnetservice.cloudnet.v2.lib.map.NetorHashMap;
import eu.cloudnetservice.cloudnet.v2.lib.server.screen.ScreenInfo;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Tareko on 04.10.2017.
 */
public final class ServerLogManager implements Runnable {

    private final NetorHashMap<String, MultiValue<String, Long>, Queue<ScreenInfo>> screenInfos = new NetorHashMap<>();

    public void append(String rnd, String serverId) {
        for (String key : screenInfos.keySet()) {
            if (this.screenInfos.getF(key).getFirst().equals(serverId)) {
                screenInfos.add(rnd,
                                new MultiValue<>(serverId, (System.currentTimeMillis() + 600000L)),
                                new ConcurrentLinkedQueue<>(this.screenInfos.getS(key)));
                return;
            }
        }

        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(serverId);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().enableScreen(minecraftServer.getServerInfo());
            screenInfos.add(rnd, new MultiValue<>(serverId, (System.currentTimeMillis() + 600000L)), new ConcurrentLinkedQueue<>());
            return;
        }

        ProxyServer proxyServer = CloudNet.getInstance().getProxy(serverId);
        if (proxyServer != null) {
            proxyServer.getWrapper().enableScreen(proxyServer.getProxyInfo());
            screenInfos.add(rnd, new MultiValue<>(serverId, (System.currentTimeMillis() + 600000L)), new ConcurrentLinkedQueue<>());
        }
    }

    public void appendScreenData(Collection<ScreenInfo> screenInfos) {
        if (screenInfos.size() != 0) {
            for (ScreenInfo screenInfo : screenInfos) {
                for (String key : this.screenInfos.keySet()) {
                    if (this.screenInfos.getF(key).getFirst().equalsIgnoreCase(screenInfo.getServiceId().getServerId())) {
                        this.screenInfos.getS(key).addAll(screenInfos);

                        while (this.screenInfos.getS(key).size() >= 64) {
                            this.screenInfos.getS(key).poll();
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    @Override
    public void run() {
        for (String key : screenInfos.keySet()) {
            if (screenInfos.getF(key).getSecond() < System.currentTimeMillis()) {
                String server = screenInfos.getF(key).getFirst();

                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(server);
                if (minecraftServer != null) {
                    minecraftServer.getWrapper().disableScreen(minecraftServer.getServerInfo());
                }

                ProxyServer proxyServer = CloudNet.getInstance().getProxy(server);
                if (proxyServer != null) {
                    proxyServer.getWrapper().disableScreen(proxyServer.getProxyInfo());
                }

                screenInfos.remove(key);
            }
        }
    }

    public NetorHashMap<String, MultiValue<String, Long>, Queue<ScreenInfo>> getScreenInfos() {
        return screenInfos;
    }
}
