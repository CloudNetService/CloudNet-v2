package eu.cloudnetservice.cloudnet.v2.lib.server;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.process.ProxyProcessData;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.service.plugin.ServerInstallablePlugin;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Class holding data about an actual process that can be used to launch a proxy.
 */
public class ProxyProcessMeta extends ProxyProcessData {

    public static final Type TYPE = TypeToken.get(ProxyProcessMeta.class).getType();

    private final ServiceId serviceId;
    private final int port;

    public ProxyProcessMeta(final String wrapperName,
                            final String proxyGroupName,
                            final int memory,
                            final List<String> javaProcessParameters,
                            final List<String> proxyProcessParameters,
                            final String templateUrl,
                            final Set<ServerInstallablePlugin> plugins,
                            final Document properties,
                            final ServiceId serviceId,
                            final int port) {
        super(wrapperName, proxyGroupName, memory, javaProcessParameters, proxyProcessParameters, templateUrl, plugins, properties);
        this.serviceId = serviceId;
        this.port = port;
    }

    public ProxyProcessMeta(final ProxyProcessData proxyProcessData,
                            final ServiceId serviceId,
                            final int port) {
        super(proxyProcessData);
        this.serviceId = serviceId;
        this.port = port;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        result = 31 * result + port;
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
        if (!super.equals(o)) {
            return false;
        }

        final ProxyProcessMeta that = (ProxyProcessMeta) o;

        if (port != that.port) {
            return false;
        }
        return Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public String toString() {
        return "ProxyProcessMeta{" +
            "serviceId=" + serviceId +
            ", port=" + port +
            "} " + super.toString();
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getPort() {
        return port;
    }

}
