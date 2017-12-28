package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * Created by Tareko on 30.07.2017.
 */
@Getter
@AllArgsConstructor
public class ProxyProcessMeta {

    private ServiceId serviceId;

    private int memory;

    private int port;

    private String[] processParameters;

    private String url;

    private Collection<ServerInstallablePlugin> downloadablePlugins;

    private Document properties;

}