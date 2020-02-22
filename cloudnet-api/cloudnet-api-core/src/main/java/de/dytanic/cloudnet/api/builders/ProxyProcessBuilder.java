package de.dytanic.cloudnet.api.builders;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.api.PacketOutStartProxy;
import de.dytanic.cloudnet.lib.process.ProxyProcessData;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Builder for a proxy process.
 * Uses {@link ProxyProcessData} for storing the data.
 */
public class ProxyProcessBuilder {
    private final ProxyProcessData proxyProcessData = new ProxyProcessData();

    private ProxyProcessBuilder() {
    }

    /**
     * Creates a new proxy process builder for a proxy of the specified proxy group.
     * This value is mandatory as proxies cannot be started without belonging to a group.
     *
     * @param proxyGroupName the name of the proxy group that the proxy will be started from.
     *
     * @return the newly created proxy process builder.
     */
    public static ProxyProcessBuilder create(String proxyGroupName) {
        return new ProxyProcessBuilder().proxyGroupName(proxyGroupName);
    }

    /**
     * Sets the name of the proxy group.
     *
     * @param proxyGroupName the name of the proxy group. Must not be null.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder proxyGroupName(String proxyGroupName) {
        Objects.requireNonNull(proxyGroupName);
        this.proxyProcessData.setProxyGroupName(proxyGroupName);
        return this;
    }

    /**
     * Sets the name of the wrapper that the proxy will be started on.
     * Set to {@code null} to not specify a wrapper.
     *
     * @param wrapperName the name of the wrapper to start the proxy on.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder wrapperName(String wrapperName) {
        this.proxyProcessData.setWrapperName(wrapperName);
        return this;
    }

    /**
     * Sets the amount of memory for the heap of the proxy process in megabytes.
     *
     * @param memory the amount of memory the proxy process should get allocated in megabytes.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder memory(int memory) {
        this.proxyProcessData.setMemory(memory);
        return this;
    }

    /**
     * Sets the parameters being passed to the Java runtime for starting the proxy process.
     *
     * @param javaProcessParameters a list containing all the parameters that will be passed onto the Java runtime. Must not be null.
     *
     * @return the proxy process builder.
     *
     * @see #proxyProcessParameters(List)
     */
    public ProxyProcessBuilder javaProcessParameters(List<String> javaProcessParameters) {
        this.proxyProcessData.getJavaProcessParameters().clear();
        this.proxyProcessData.getJavaProcessParameters().addAll(javaProcessParameters);
        return this;
    }

    /**
     * Sets the parameters being passed to the proxy executable.
     *
     * @param proxyProcessParameters a list containing all the parameters that will be passed to the proxy executable. Must not be null.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder proxyProcessParameters(List<String> proxyProcessParameters) {
        this.proxyProcessData.getProxyProcessParameters().clear();
        this.proxyProcessData.getProxyProcessParameters().addAll(proxyProcessParameters);
        return this;
    }

    /**
     * Sets the URL to download the template from.
     * Setting this to not {@code null} overrides the template of the proxy.
     *
     * @param templateUrl the url to download the template from.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder templateUrl(String templateUrl) {
        this.proxyProcessData.setTemplateUrl(templateUrl);
        return this;
    }

    /**
     * Sets the plugins that will be installed to this proxy prior to it starting.
     *
     * @param plugins a set of plugins that will be installed.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder plugins(Set<ServerInstallablePlugin> plugins) {
        this.proxyProcessData.getPlugins().clear();
        this.proxyProcessData.getPlugins().addAll(plugins);
        return this;
    }

    /**
     * Sets the additional properties that can be accessed at all stages
     * in the process' life-cycle.
     *
     * @param properties additional properties for the proxy process.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder properties(Document properties) {
        this.proxyProcessData.setProperties(properties);
        return this;
    }

    /**
     * Adds a parameter for the Java runtime.
     *
     * @param parameter the parameter to add.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder addJavaProcessParameter(String parameter) {
        this.proxyProcessData.getJavaProcessParameters().add(parameter);
        return this;
    }

    /**
     * Adds a parameter for the proxy executable.
     *
     * @param parameter the parameter to add.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder addProxyProcessParameter(String parameter) {
        this.proxyProcessData.getProxyProcessParameters().add(parameter);
        return this;
    }

    /**
     * Adds a plugin that will be installed prior to the proxy starting.
     *
     * @param plugin the plugin to install.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder addPlugin(ServerInstallablePlugin plugin) {
        this.proxyProcessData.getPlugins().add(plugin);
        return this;
    }

    /**
     * Requests the master to initiate this proxy startup.
     * This will trigger a sequence of messages to be passed between the calling service,
     * the master and the wrapper that will start the proxy.
     * <p>
     * Once the proxy is started, the returned future will be completed and contains the
     * process metadata that was valid at the time of starting up.
     * <p>
     * Note that the delay between requesting the start (ie. this method) and the actual
     * completion of the returned future is indefinite and the future may not actually
     * be completed at all-
     *
     * @return a future that will be completed once the proxy is connected to the cloud network.
     */
    public CompletableFuture<ProxyProcessMeta> startProxy() {
        final UUID uuid = UUID.randomUUID();
        this.proxyProcessData.getProperties().append("cloudnet:requestId", uuid);
        CloudAPI.getInstance().getNetworkConnection().sendAsynchronous(
            new PacketOutStartProxy(this.proxyProcessData)
        );
        return CloudAPI.getInstance().getCloudService().waitForProxy(uuid);
    }

    public String getWrapperName() {
        return proxyProcessData.getWrapperName();
    }

    public String getProxyGroupName() {
        return proxyProcessData.getProxyGroupName();
    }

    public int getMemory() {
        return proxyProcessData.getMemory();
    }

    public List<String> getJavaProcessParameters() {
        return proxyProcessData.getJavaProcessParameters();
    }

    public List<String> getProxyProcessParameters() {
        return proxyProcessData.getProxyProcessParameters();
    }

    public String getTemplateUrl() {
        return proxyProcessData.getTemplateUrl();
    }

    public Set<ServerInstallablePlugin> getPlugins() {
        return proxyProcessData.getPlugins();
    }

    public Document getProperties() {
        return proxyProcessData.getProperties();
    }

    public ProxyProcessData getProxyProcessData() {
        return proxyProcessData;
    }

}
