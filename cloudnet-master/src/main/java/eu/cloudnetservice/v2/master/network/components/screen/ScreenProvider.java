package eu.cloudnetservice.v2.master.network.components.screen;

import de.dytanic.cloudnet.lib.service.ServiceId;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Tareko on 20.08.2017.
 */
public class ScreenProvider {

    private Map<String, EnabledScreen> screens = new ConcurrentHashMap<>();

    private ServiceId mainServiceId;

    public void handleEnableScreen(ServiceId serviceId, Wrapper wrapper) {
        screens.put(serviceId.getServerId(), new EnabledScreen(serviceId, wrapper));
    }

    public void handleDisableScreen(ServiceId serviceId) {
        screens.remove(serviceId.getServerId());
    }

    public void disableScreen(String server) {
        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(server);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().disableScreen(minecraftServer.getServerInfo());
            return;
        }

        ProxyServer proxyServer = CloudNet.getInstance().getProxy(server);
        if (proxyServer != null) {
            proxyServer.getWrapper().disableScreen(proxyServer.getProxyInfo());
        }
    }

    public Map<String, EnabledScreen> getScreens() {
        return screens;
    }

    public ServiceId getMainServiceId() {
        return mainServiceId;
    }

    public void setMainServiceId(ServiceId mainServiceId) {
        this.mainServiceId = mainServiceId;
    }
}
