package de.dytanic.cloudnet.lib.service;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by Tareko on 18.07.2017.
 */
public final class ServiceId {
    private String group;
    private int id;
    private UUID uniqueId;
    private String wrapperId;
    private String serverId;
    private String gameId;

    public ServiceId(String group, int id, UUID uniqueId, String wrapperId) {
        this.group = group;
        this.id = id;
        this.uniqueId = uniqueId;
        this.wrapperId = wrapperId;

        this.serverId = group + '-' + id;
        this.gameId = uniqueId.toString().split("-")[0];
    }

    public ServiceId(String group, int id, UUID uniqueId, String wrapperId, String serverId) {
        this.group = group;
        this.id = id;
        this.uniqueId = uniqueId;
        this.wrapperId = wrapperId;

        this.serverId = serverId;
        this.gameId = uniqueId.toString().split("-")[0];
    }

    @Override
    public int hashCode() {
        int result = group != null ? group.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (uniqueId != null ? uniqueId.hashCode() : 0);
        result = 31 * result + (wrapperId != null ? wrapperId.hashCode() : 0);
        result = 31 * result + (serverId != null ? serverId.hashCode() : 0);
        result = 31 * result + (gameId != null ? gameId.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceId)) {
            return false;
        }
        final ServiceId serviceId = (ServiceId) o;
        return id == serviceId.id && Objects.equals(group, serviceId.group) && Objects.equals(uniqueId,
                                                                                              serviceId.uniqueId) && Objects.equals(
            wrapperId,
            serviceId.wrapperId) && Objects.equals(serverId, serviceId.serverId) && Objects.equals(gameId, serviceId.gameId);
    }

    @Override
    public String toString() {
        return this.serverId + '#' + this.uniqueId.toString();
    }

    public String getServerId() {
        return serverId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getGroup() {
        return group;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public int getId() {
        return id;
    }

    public String getGameId() {
        return gameId;
    }

}
