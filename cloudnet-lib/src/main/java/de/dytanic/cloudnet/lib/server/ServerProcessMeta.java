package de.dytanic.cloudnet.lib.server;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Tareko on 30.07.2017.
 */
public class ServerProcessMeta {

    public static final Type TYPE = TypeToken.get(ServerProcessMeta.class).getType();

    private ServiceId serviceId;
    private int memory;
    private boolean priorityStop;
    private String url;
    private List<String> processParameters;
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
                             List<String> processParameters,
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

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + memory;
        result = 31 * result + (priorityStop ? 1 : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (processParameters != null ? processParameters.hashCode() : 0);
        result = 31 * result + (onlineMode ? 1 : 0);
        result = 31 * result + (downloadablePlugins != null ? downloadablePlugins.hashCode() : 0);
        result = 31 * result + (serverConfig != null ? serverConfig.hashCode() : 0);
        result = 31 * result + (customServerDownload != null ? customServerDownload.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (serverProperties != null ? serverProperties.hashCode() : 0);
        result = 31 * result + (template != null ? template.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ServerProcessMeta that = (ServerProcessMeta) o;

        if (memory != that.memory) {
            return false;
        }
        if (priorityStop != that.priorityStop) {
            return false;
        }
        if (onlineMode != that.onlineMode) {
            return false;
        }
        if (port != that.port) {
            return false;
        }
        if (!Objects.equals(serviceId, that.serviceId)) {
            return false;
        }
        if (!Objects.equals(url, that.url)) {
            return false;
        }
        if (!Objects.equals(processParameters, that.processParameters)) {
            return false;
        }
        if (!Objects.equals(downloadablePlugins, that.downloadablePlugins)) {
            return false;
        }
        if (!Objects.equals(serverConfig, that.serverConfig)) {
            return false;
        }
        if (!Objects.equals(customServerDownload, that.customServerDownload)) {
            return false;
        }
        if (!Objects.equals(serverProperties, that.serverProperties)) {
            return false;
        }
        return Objects.equals(template, that.template);
    }

    @Override
    public String toString() {
        return "ServerProcessMeta{" +
            "serviceId=" + serviceId +
            ", memory=" + memory +
            ", priorityStop=" + priorityStop +
            ", url='" + url + '\'' +
            ", processParameters=" + processParameters +
            ", onlineMode=" + onlineMode +
            ", downloadablePlugins=" + downloadablePlugins +
            ", serverConfig=" + serverConfig +
            ", customServerDownload='" + customServerDownload + '\'' +
            ", port=" + port +
            ", serverProperties=" + serverProperties +
            ", template=" + template +
            '}';
    }

    public String getUrl() {
        return url;
    }

    public List<String> getProcessParameters() {
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
