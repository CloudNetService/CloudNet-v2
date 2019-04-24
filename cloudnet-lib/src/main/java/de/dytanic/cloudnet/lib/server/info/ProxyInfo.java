package de.dytanic.cloudnet.lib.server.info;

import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.service.ServiceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

/**
 * Created by Tareko on 25.05.2017.
 */
@AllArgsConstructor
@Getter
@ToString
public class ProxyInfo {

    private ServiceId serviceId;

    private String host;
    private int port;
    private boolean online;
    private List<MultiValue<UUID, String>> players;
    private int memory;
    private int onlineCount;

    public SimpleProxyInfo toSimple() {
        return new SimpleProxyInfo(serviceId, online, host, port, memory, onlineCount);
    }

}
