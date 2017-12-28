package de.dytanic.cloudnet.lib.server.info;

import de.dytanic.cloudnet.lib.service.ServiceId;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 02.07.2017.
 */
@Getter
@AllArgsConstructor
public class SimpleProxyInfo {

    private ServiceId serviceId;
    private boolean online;
    private String hostName;
    private int port;
    private int memory;
    private int onlineCount;

}