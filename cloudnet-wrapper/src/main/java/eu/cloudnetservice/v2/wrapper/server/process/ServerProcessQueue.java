package eu.cloudnetservice.v2.wrapper.server.process;

import eu.cloudnetservice.v2.lib.NetworkUtils;
import eu.cloudnetservice.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.v2.wrapper.server.BungeeCord;
import eu.cloudnetservice.v2.wrapper.server.GameServer;

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
        this.servers.offer(new ServerProcess(serverProcessMeta));
    }

    public void putProcess(ProxyProcessMeta proxyProcessMeta) {
        this.proxies.offer(proxyProcessMeta);
    }

    @Override
    public void run() {
        {
            short i = 0;

            while (running && !servers.isEmpty() &&
                (CloudNetWrapper.getInstance().getWrapperConfig().getPercentOfCPUForANewServer() == 0D ||
                    NetworkUtils.cpuUsage() <= CloudNetWrapper.getInstance().getWrapperConfig().getPercentOfCPUForANewServer())) {
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
            while (running && !proxies.isEmpty() &&
                (CloudNetWrapper.getInstance().getWrapperConfig().getPercentOfCPUForANewProxy() == 0 ||
                    NetworkUtils.cpuUsage() <= CloudNetWrapper.getInstance().getWrapperConfig().getPercentOfCPUForANewProxy())) {
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

                    BungeeCord bungeeCord = new BungeeCord(serverProcess,
                                                           CloudNetWrapper.getInstance()
                                                                          .getProxyGroups()
                                                                          .get(serverProcess.getServiceId().getGroup()));

                    try {
                        System.out.println("Fetching entry [" + bungeeCord.getServiceId() + ']');
                        if (!bungeeCord.bootstrap()) {
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
