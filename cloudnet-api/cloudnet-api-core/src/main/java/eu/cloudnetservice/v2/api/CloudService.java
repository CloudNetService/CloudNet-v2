package eu.cloudnetservice.v2.api;

import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * An interface for cloud services.
 * These include the proxy and the server.
 * This guarantees a base level of functionality across all services.
 */
public interface CloudService {

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

    /**
     * Returns a future that will be completed once the proxy in the proxy process has been started
     * and has connected to the cloud network.
     *
     * @param uuid the unique id of the process to wait for.
     *
     * @return a future that completes once the process of the proxy has been started.
     */
    CompletableFuture<ProxyProcessMeta> waitForProxy(UUID uuid);

    /**
     * Returns a future that will be completed once the server in the server process has been started
     * and has connected to the cloud network.
     *
     * @param uuid the unique id of the process to wait for.
     *
     * @return a future that completes once the process of the server has been started.
     */
    CompletableFuture<ServerProcessMeta> waitForServer(UUID uuid);

}
