package de.dytanic.cloudnet.lib.cloudserver;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Tareko on 17.10.2017.
 */
public class CloudServerMeta {

    public static final Type TYPE = TypeToken.get(CloudServerMeta.class).getType();
    private ServiceId serviceId;
    private int memory;
    private boolean priorityStop;
    private String[] processParameters;
    private Collection<ServerInstallablePlugin> plugins;
    private ServerConfig serverConfig;
    private int port;
    private String templateName;
    private Properties serverProperties;
    private ServerGroupType serverGroupType;
    private Template template;
    public CloudServerMeta(ServiceId serviceId,
                           int memory,
                           boolean priorityStop,
                           String[] processParameters,
                           Collection<ServerInstallablePlugin> plugins,
                           ServerConfig serverConfig,
                           int port,
                           String templateName,
                           Properties properties,
                           ServerGroupType serverGroupType) {
        this.serviceId = serviceId;
        this.memory = memory;
        this.priorityStop = priorityStop;
        this.processParameters = processParameters;
        this.plugins = plugins;
        this.serverConfig = serverConfig;
        this.port = port;
        this.templateName = templateName;
        this.serverProperties = properties;
        this.serverGroupType = serverGroupType;
        this.template = new Template(templateName, TemplateResource.MASTER, null, new String[0], new ArrayList<>());
    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + memory;
        result = 31 * result + (priorityStop ? 1 : 0);
        result = 31 * result + Arrays.hashCode(processParameters);
        result = 31 * result + (plugins != null ? plugins.hashCode() : 0);
        result = 31 * result + (serverConfig != null ? serverConfig.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (templateName != null ? templateName.hashCode() : 0);
        result = 31 * result + (serverProperties != null ? serverProperties.hashCode() : 0);
        result = 31 * result + (serverGroupType != null ? serverGroupType.hashCode() : 0);
        result = 31 * result + (template != null ? template.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudServerMeta)) {
            return false;
        }

        final CloudServerMeta that = (CloudServerMeta) o;

        if (memory != that.memory) {
            return false;
        }
        if (priorityStop != that.priorityStop) {
            return false;
        }
        if (port != that.port) {
            return false;
        }
        if (!Objects.equals(serviceId, that.serviceId)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(processParameters, that.processParameters)) {
            return false;
        }
        if (!Objects.equals(plugins, that.plugins)) {
            return false;
        }
        if (!Objects.equals(serverConfig, that.serverConfig)) {
            return false;
        }
        if (!Objects.equals(templateName, that.templateName)) {
            return false;
        }
        if (!Objects.equals(serverProperties, that.serverProperties)) {
            return false;
        }
        if (serverGroupType != that.serverGroupType) {
            return false;
        }
        return Objects.equals(template, that.template);
    }

    @Override
    public String toString() {
        return "de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta{" +
            "serviceId=" + serviceId +
            ", memory=" + memory +
            ", priorityStop=" + priorityStop +
            ", processParameters=" + Arrays.toString(processParameters) +
            ", plugins=" + plugins +
            ", serverConfig=" + serverConfig +
            ", port=" + port +
            ", templateName='" + templateName + '\'' +
            ", serverProperties=" + serverProperties +
            ", serverGroupType=" + serverGroupType +
            ", template=" + template +
            '}';
    }

    public Template getTemplate() {
        return template;
    }

    public int getMemory() {
        return memory;
    }

    public Properties getServerProperties() {
        return serverProperties;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getPort() {
        return port;
    }

    public String[] getProcessParameters() {
        return processParameters;
    }

    public ServerGroupType getServerGroupType() {
        return serverGroupType;
    }

    public Collection<ServerInstallablePlugin> getPlugins() {
        return plugins;
    }

    public String getTemplateName() {
        return templateName;
    }

    public boolean isPriorityStop() {
        return priorityStop;
    }
}
