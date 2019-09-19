/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.server.process;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.CloudGameServer;
import de.dytanic.cloudnetwrapper.server.GameServer;
import de.dytanic.cloudnetwrapper.server.ServerStage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerProcessQueue implements Runnable {

    private final Queue<ServerProcess> servers = new ConcurrentLinkedQueue<>();
    private final Queue<ProxyProcessMeta> proxys = new ConcurrentLinkedQueue<>();
    private final Queue<CloudServerMeta> cloudServers = new ConcurrentLinkedQueue<>();
    private final int process_queue_size;
    private volatile boolean running = true;

    public ServerProcessQueue(int process_queue_size) {
        this.process_queue_size = process_queue_size;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Queue<ServerProcess> getServers() {
        return servers;
    }

    public int getProcess_queue_size() {
        return process_queue_size;
    }

    public Queue<ProxyProcessMeta> getProxys() {
        return proxys;
    }

    public Queue<CloudServerMeta> getCloudServers() {
        return cloudServers;
    }

    public void putProcess(ServerProcessMeta serverProcessMeta) {
        this.servers.offer(new ServerProcess(serverProcessMeta, ServerStage.SETUP));
    }

    public void putProcess(CloudServerMeta serverProcessMeta) {
        this.cloudServers.offer(serverProcessMeta);
    }

    public void putProcess(ProxyProcessMeta proxyProcessMeta) {
        this.proxys.offer(proxyProcessMeta);
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
                while (running && !proxys.isEmpty() && (CloudNetWrapper.getInstance()
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

                    ProxyProcessMeta serverProcess = proxys.poll();

                    if (!CloudNetWrapper.getInstance().getProxyGroups().containsKey(serverProcess.getServiceId().getGroup())) {
                        this.proxys.add(serverProcess);
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
                                this.proxys.add(serverProcess);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            this.proxys.add(serverProcess);
                        }
                    } else {
                        this.proxys.add(serverProcess);
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
        TaskScheduler.runtimeScheduler().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    gameServer.bootstrap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void patchAsync(CloudServerMeta cloudServerMeta) {
        CloudGameServer cloudGameServer = new CloudGameServer(cloudServerMeta);
        TaskScheduler.runtimeScheduler().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    cloudGameServer.bootstrap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void patchAsync(ProxyProcessMeta proxyProcessMeta) {
        BungeeCord bungeeCord = new BungeeCord(proxyProcessMeta,
                                               CloudNetWrapper.getInstance()
                                                              .getProxyGroups()
                                                              .get(proxyProcessMeta.getServiceId().getGroup()));

        if (!CloudNetWrapper.getInstance().getProxyGroups().containsKey(proxyProcessMeta.getServiceId().getGroup())) {
            this.proxys.add(proxyProcessMeta);
            return;
        }

        TaskScheduler.runtimeScheduler().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    bungeeCord.bootstrap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
