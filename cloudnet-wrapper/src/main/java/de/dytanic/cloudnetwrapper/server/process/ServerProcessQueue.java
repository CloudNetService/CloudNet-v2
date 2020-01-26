package de.dytanic.cloudnetwrapper.server.process;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.GameServer;
import de.dytanic.cloudnetwrapper.server.ServerStage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerProcessQueue implements Runnable {

    private final Queue<ServerProcess> servers = new ConcurrentLinkedQueue<>();
    private final Queue<ProxyProcessMeta> proxies = new ConcurrentLinkedQueue<>();
    private final int processQueueSize;
    private volatile boolean running = true;

    public ServerProcessQueue(int processQueueSize) {
        this.processQueueSize = processQueueSize;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Queue<ServerProcess> getServers() {
        return servers;
    }

    public int getProcessQueueSize() {
        return processQueueSize;
    }

    public Queue<ProxyProcessMeta> getProxies() {
        return proxies;
    }

    public void putProcess(ServerProcessMeta serverProcessMeta) {
        this.servers.offer(new ServerProcess(serverProcessMeta, ServerStage.SETUP));
    }

    public void putProcess(ProxyProcessMeta proxyProcessMeta) {
        this.proxies.offer(proxyProcessMeta);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            NetworkUtils.sleepUninterruptedly(500);

            {
                short i = 0;

                while (running && !servers.isEmpty() && (CloudNetWrapper.getInstance()
                                                                        .getWrapperConfig()
                                                                        .getPercentOfCPUForANewServer() == 0D || NetworkUtils.cpuUsage() <= CloudNetWrapper
                    .getInstance()
                    .getWrapperConfig()
                    .getPercentOfCPUForANewServer())) {
                    i++;
                    if (i == 3) {
                        break;
                    }

                    int memory = CloudNetWrapper.getInstance().getUsedMemory();

                    ServerProcess serverProcess = servers.poll();

                    if (!CloudNetWrapper.getInstance().getServerGroups().containsKey(serverProcess.getMeta().getServiceId().getGroup())) {
                        this.servers.add(serverProcess);
                        continue;
                    }

                    if ((memory + serverProcess.getMeta().getMemory()) < CloudNetWrapper.getInstance().getMaxMemory()) {
                        GameServer gameServer = null;
                        try {
                            System.out.println("Fetching entry [" + serverProcess.getMeta().getServiceId() + ']');
                            gameServer = new GameServer(serverProcess,
                                                        ServerStage.SETUP,
                                                        CloudNetWrapper.getInstance()
                                                                       .getServerGroups()
                                                                       .get(serverProcess.getMeta().getServiceId().getGroup()));
                            if (!gameServer.bootstrap()) {
                                this.servers.add(serverProcess);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            this.servers.add(serverProcess);
                        }
                    } else {
                        this.servers.add(serverProcess);
                    }
                }
            }

            {
                short i = 0;
                while (running && !proxies.isEmpty() && (CloudNetWrapper.getInstance()
                                                                        .getWrapperConfig()
                                                                        .getPercentOfCPUForANewProxy() == 0 || NetworkUtils.cpuUsage() <= CloudNetWrapper
                    .getInstance()
                    .getWrapperConfig()
                    .getPercentOfCPUForANewProxy())) {
                    i++;
                    if (i == 3) {
                        break;
                    }
                    int memory = CloudNetWrapper.getInstance().getUsedMemory();

                    ProxyProcessMeta serverProcess = proxies.poll();

                    if (!CloudNetWrapper.getInstance().getProxyGroups().containsKey(serverProcess.getServiceId().getGroup())) {
                        this.proxies.add(serverProcess);
                        continue;
                    }

                    if ((memory + serverProcess.getMemory()) < CloudNetWrapper.getInstance().getMaxMemory()) {

                        BungeeCord gameServer = new BungeeCord(serverProcess,
                                                               CloudNetWrapper.getInstance()
                                                                              .getProxyGroups()
                                                                              .get(serverProcess.getServiceId().getGroup()));

                        try {
                            System.out.println("Fetching entry [" + gameServer.getServiceId() + ']');
                            if (!gameServer.bootstrap()) {
                                this.proxies.add(serverProcess);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            this.proxies.add(serverProcess);
                        }
                    } else {
                        this.proxies.add(serverProcess);
                    }
                }
            }
        }
    }

    public void patchAsync(ServerProcessMeta process) {
        if (!CloudNetWrapper.getInstance().getServerGroups().containsKey(process.getServiceId().getGroup())) {
            this.servers.add(new ServerProcess(process, ServerStage.SETUP));
            return;
        }
        GameServer gameServer = new GameServer(new ServerProcess(process, ServerStage.SETUP),
                                               ServerStage.SETUP,
                                               CloudNetWrapper.getInstance().getServerGroups().get(process.getServiceId().getGroup()));
        NetworkUtils.getExecutor().submit(() -> {
            try {
                gameServer.bootstrap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void patchAsync(ProxyProcessMeta proxyProcessMeta) {
        BungeeCord bungeeCord = new BungeeCord(proxyProcessMeta,
                                               CloudNetWrapper.getInstance()
                                                              .getProxyGroups()
                                                              .get(proxyProcessMeta.getServiceId().getGroup()));

        if (!CloudNetWrapper.getInstance().getProxyGroups().containsKey(proxyProcessMeta.getServiceId().getGroup())) {
            this.proxies.add(proxyProcessMeta);
            return;
        }

        NetworkUtils.getExecutor().submit(() -> {
            try {
                bungeeCord.bootstrap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
