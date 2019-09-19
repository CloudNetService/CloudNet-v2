package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 30.07.2017.
 */
public class ServerProcessMeta {

    private ServiceId serviceId;

    private int memory;

    private boolean priorityStop;

    private String url;

    private String[] processParameters;

    private boolean onlineMode;

    private Collection<ServerInstallablePlugin> downloadablePlugins;

    private ServerConfig serverConfig;

    private String customServerDownload;

    private int port;

    private Properties serverProperties;

    private Template template;

    public ServerProcessMeta(ServiceId serviceId,
                             int memory,
                             boolean priorityStop,
                             String url,
                             String[] processParameters,
                             boolean onlineMode,
                             Collection<ServerInstallablePlugin> downloadablePlugins,
                             ServerConfig serverConfig,
                             String customServerDownload,
                             int port,
                             Properties serverProperties,
                             Template template) {
        this.serviceId = serviceId;
        this.memory = memory;
        this.priorityStop = priorityStop;
        this.url = url;
        this.processParameters = processParameters;
        this.onlineMode = onlineMode;
        this.downloadablePlugins = downloadablePlugins;
        this.serverConfig = serverConfig;
        this.customServerDownload = customServerDownload;
        this.port = port;
        this.serverProperties = serverProperties;
        this.template = template;
    }

    public String getUrl() {
        return url;
    }

    public String[] getProcessParameters() {
        return processParameters;
    }

    public Collection<ServerInstallablePlugin> getDownloadablePlugins() {
        return downloadablePlugins;
    }

    public int getPort() {
        return port;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getMemory() {
        return memory;
    }

    public Template getTemplate() {
        return template;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public Properties getServerProperties() {
        return serverProperties;
    }

    public String getCustomServerDownload() {
        return customServerDownload;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public boolean isPriorityStop() {
        return priorityStop;
    }
}
