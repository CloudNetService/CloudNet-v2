package de.dytanic.cloudnet.api;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 10.01.2018.
 */
public interface ICloudService {

    /**
     * Gets a player currently connected to this service by its unique ID.
     *
     * @param uniqueId the UUID of the player to get.
     *
     * @return the cloud player instance associated to the given unique ID.
     */
    CloudPlayer getCachedPlayer(UUID uniqueId);

    /**
     * Gets a player currently connected to this service by its name.
     * This method may be case-insensitive.
     *
     * @param name the name of the player to get.
     *
     * @return the cloud player with the given name.
     */
    CloudPlayer getCachedPlayer(String name);

    /**
     * Returns whether or not the queried service is a proxy instance.
     *
     * @return whether or not the service is a proxy instance.
     */
    boolean isProxyInstance();

    /**
     * Returns the currently accessible and connected servers.
     * May throw an exception when called on server instances,
     * check with {@link #isProxyInstance()} before calling this method.
     *
     * @return a map of all currently connected servers.
     */
    Map<String, ServerInfo> getServers();

}