package de.dytanic.cloudnet.lib.server;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Created by Tareko on 30.07.2017.
 */
public class ProxyProcessMeta {

    public static final Type TYPE = TypeToken.get(ProxyProcessMeta.class).getType();

    private final ServiceId serviceId;
    private final int memory;
    private final int port;
    private final String[] processParameters;
    private final String url;
    private final Collection<ServerInstallablePlugin> downloadablePlugins;
    private final Document properties;

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

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + memory;
        result = 31 * result + port;
        result = 31 * result + Arrays.hashCode(processParameters);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (downloadablePlugins != null ? downloadablePlugins.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProxyProcessMeta)) {
            return false;
        }

        final ProxyProcessMeta that = (ProxyProcessMeta) o;

        if (memory != that.memory) {
            return false;
        }
        if (port != that.port) {
            return false;
        }
        if (!Objects.equals(serviceId, that.serviceId)) {
            return false;
        }
        if (!Arrays.equals(processParameters, that.processParameters)) {
            return false;
        }
        if (!Objects.equals(url, that.url)) {
            return false;
        }
        if (!Objects.equals(downloadablePlugins, that.downloadablePlugins)) {
            return false;
        }
        return Objects.equals(properties, that.properties);
    }

    @Override
    public String toString() {
        return "de.dytanic.cloudnet.lib.server.ProxyProcessMeta{" +
            "serviceId=" + serviceId +
            ", memory=" + memory +
            ", port=" + port +
            ", processParameters=" + Arrays.toString(processParameters) +
            ", url='" + url + '\'' +
            ", downloadablePlugins=" + downloadablePlugins +
            ", properties=" + properties +
            '}';
    }
}
