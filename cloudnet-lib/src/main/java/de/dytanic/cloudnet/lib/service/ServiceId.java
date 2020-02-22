package de.dytanic.cloudnet.lib.service;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Tareko on 18.07.2017.
 */
public final class ServiceId {

    public static final Type TYPE = TypeToken.get(ServiceId.class).getType();

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceId)) {
            return false;
        }

        final ServiceId serviceId = (ServiceId) o;

        if (id != serviceId.id) {
            return false;
        }
        if (!Objects.equals(group, serviceId.group)) {
            return false;
        }
        if (!Objects.equals(uniqueId, serviceId.uniqueId)) {
            return false;
        }
        if (!Objects.equals(wrapperId, serviceId.wrapperId)) {
            return false;
        }
        if (!Objects.equals(serverId, serviceId.serverId)) {
            return false;
        }
        return Objects.equals(gameId, serviceId.gameId);
    }

    @Override
    public String toString() {
        return this.serverId + '#' + this.uniqueId;
    }
}
