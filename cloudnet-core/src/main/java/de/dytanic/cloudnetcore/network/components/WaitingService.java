package de.dytanic.cloudnetcore.network.components;

import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;

import java.util.Objects;

public class WaitingService {
    private final int port;
    private final int usedMemory;
    private final ServiceId serviceId;
    private final Template template;

    public WaitingService(final int port,
                          final int usedMemory,
                          final ServiceId serviceId,
                          final Template template) {
        this.port = port;
        this.usedMemory = usedMemory;
        this.serviceId = serviceId;
        this.template = template;
    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + usedMemory;
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        result = 31 * result + (template != null ? template.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WaitingService)) {
            return false;
        }

        final WaitingService that = (WaitingService) o;

        if (port != that.port) {
            return false;
        }
        if (usedMemory != that.usedMemory) {
            return false;
        }
        if (!Objects.equals(serviceId, that.serviceId)) {
            return false;
        }
        return Objects.equals(template, that.template);
    }

    @Override
    public String toString() {
        return "de.dytanic.cloudnetcore.network.components.WaitingService{" +
            "port=" + port +
            ", usedMemory=" + usedMemory +
            ", serviceId=" + serviceId +
            ", template=" + template +
            '}';
    }

    public int getPort() {
        return port;
    }

    public int getUsedMemory() {
        return usedMemory;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public Template getTemplate() {
        return template;
    }
}
