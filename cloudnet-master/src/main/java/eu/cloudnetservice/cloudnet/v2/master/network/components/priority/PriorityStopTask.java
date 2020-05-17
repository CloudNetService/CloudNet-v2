package eu.cloudnetservice.cloudnet.v2.master.network.components.priority;

import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;

import java.util.concurrent.Future;

/**
 * Task for automatically stopping {@link MinecraftServer} instances based on their priority
 * service configuration.
 */
public final class PriorityStopTask implements Runnable {

    private final MinecraftServer minecraftServer;
    private final int originalTime;
    private int time;
    private Future<?> future;

    public PriorityStopTask(final MinecraftServer minecraftServer,
                            final int time) {
        this.time = time;
        this.originalTime = time;
        this.minecraftServer = minecraftServer;
    }

    @Override
    public void run() {

        if (this.future == null) {
            return;
        }

        if (this.minecraftServer != null) {
            if (this.minecraftServer.getServerInfo().getOnlineCount() == 0) {
                this.time--;
            } else {
                this.time = this.originalTime;
            }
            if (this.time <= 0) {
                this.minecraftServer.getWrapper().stopServer(this.minecraftServer);
                this.future.cancel(false);
            }
        }
    }

    public void setFuture(final Future<?> future) {
        this.future = future;
    }
}
