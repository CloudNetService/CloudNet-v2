package de.dytanic.cloudnet.lib.service;

import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * Created by Tareko on 18.07.2017.
 */
@EqualsAndHashCode
public final class ServiceId {

    private String group;

    private int id;

    private UUID uniqueId;

    private String wrapperId;

    private String serverId;

    private String gameId;

    public ServiceId(String group, int id, UUID uniqueId, String wrapperId)
    {
        this.group = group;
        this.id = id;
        this.uniqueId = uniqueId;
        this.wrapperId = wrapperId;

        this.serverId = group + "-" + id;
        this.gameId = uniqueId.toString().split("-")[0];
    }

    public ServiceId(String group, int id, UUID uniqueId, String wrapperId, String serverId)
    {
        this.group = group;
        this.id = id;
        this.uniqueId = uniqueId;
        this.wrapperId = wrapperId;

        this.serverId = serverId;
        this.gameId = uniqueId.toString().split("-")[0];
    }

    public String getServerId()
    {
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
    public String toString()
    {
        return group + "-" + id + "#" + uniqueId.toString();
    }

}