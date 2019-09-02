package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Collection;

/**
 * Created by Tareko on 30.07.2017.
 */
public class ProxyProcessMeta {

    private ServiceId serviceId;

    private int memory;

    private int port;

    private String[] processParameters;

    private String url;

    private Collection<ServerInstallablePlugin> downloadablePlugins;

    private Document properties;

    public ProxyProcessMeta(ServiceId serviceId,
                            int memory,
                            int port,
                            String[] processParameters,
                            String url,
                            Collection<ServerInstallablePlugin> downloadablePlugins,
                            Document properties) {
        this.serviceId = serviceId;
        this.memory = memory;
        this.port = port;
        this.processParameters = processParameters;
        this.url = url;
        this.downloadablePlugins = downloadablePlugins;
        this.properties = properties;
    }

    public int getMemory() {
        return memory;
    }

    public Document getProperties() {
        return properties;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getPort() {
        return port;
    }

    public Collection<ServerInstallablePlugin> getDownloadablePlugins() {
        return downloadablePlugins;
    }

    public String getUrl() {
        return url;
    }

    public String[] getProcessParameters() {
        return processParameters;
    }
}
