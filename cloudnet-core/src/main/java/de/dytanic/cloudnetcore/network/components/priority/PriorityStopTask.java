package de.dytanic.cloudnetcore.network.components.priority;

import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.INetworkComponent;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.concurrent.Future;

/**
 * Created by Tareko on 20.08.2017.
 */
public final class PriorityStopTask implements Runnable {

    private String wrapper;

    private INetworkComponent iNetworkComponent;

    private int time;

    private Future<?> future;

    public PriorityStopTask(Wrapper wrapper, INetworkComponent iNetworkComponent, int time) {
        this.wrapper = wrapper.getServerId();
        this.iNetworkComponent = iNetworkComponent;
        this.time = time;
    }

    @Override
    public void run() {

        if (iNetworkComponent instanceof ProxyServer) {
            if (!getWrapperInstance().getProxys().containsKey(iNetworkComponent.getServerId()) && future != null) {
                future.cancel(true);
            }
        }

        if (iNetworkComponent instanceof MinecraftServer) {
            if (!getWrapperInstance().getServers().containsKey(iNetworkComponent.getServerId()) && future != null) {
                future.cancel(true);
            }
        }

        if (iNetworkComponent.getChannel() != null) {
            if (iNetworkComponent instanceof ProxyServer) {
                if (((ProxyServer) iNetworkComponent).getProxyInfo().getOnlineCount() == 0) {
                    time--;
                }
            }

            if (iNetworkComponent instanceof MinecraftServer) {
                if (((MinecraftServer) iNetworkComponent).getServerInfo().getOnlineCount() == 0) {
                    time--;
                }
            }
        }

        if (time == 0) {
            if (iNetworkComponent instanceof ProxyServer) {
                getWrapperInstance().stopProxy(((ProxyServer) iNetworkComponent));
            }

            if (iNetworkComponent instanceof MinecraftServer) {
                getWrapperInstance().stopServer(((MinecraftServer) iNetworkComponent));
            }

            if (future != null) {
                future.cancel(true);
            }
        }
    }

    private Wrapper getWrapperInstance() {
        return CloudNet.getInstance().getWrappers().get(wrapper);
    }

    public int getTime() {
        return time;
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public String getWrapper() {
        return wrapper;
    }
}
