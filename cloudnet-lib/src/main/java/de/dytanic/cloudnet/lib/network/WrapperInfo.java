package de.dytanic.cloudnet.lib.network;

import lombok.ToString;

/**
 * Created by Tareko on 29.06.2017.
 */
@ToString
public class WrapperInfo {

    private String serverId;
    private String hostName;
    private String version;
    private boolean ready;
    private int availableProcessors;
    private int startPort;
    private int process_queue_size;
    private int memory;

    public WrapperInfo(String serverId, String hostName, String version, boolean ready, int availableProcessors, int startPort, int process_queue_size, int memory) {
        this.serverId = serverId;
        this.hostName = hostName;
        this.version = version;
        this.ready = ready;
        this.availableProcessors = availableProcessors;
        this.startPort = startPort;
        this.process_queue_size = process_queue_size;
        this.memory = memory;
    }

    public String getServerId() {
        return serverId;
    }

    public int getStartPort() {
        return startPort;
    }

    public int getProcess_queue_size() {
        return process_queue_size;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public int getMemory() {
        return memory;
    }

    public String getHostName() {
        return hostName;
    }

    public String getVersion() {
        return version;
    }
}
