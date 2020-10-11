/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.lib.process;

import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.service.plugin.ServerInstallablePlugin;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Builder for a proxy process.
 * Uses {@link ProxyProcessData} for storing the data.
 */
public abstract class ProxyProcessBuilder {
    private final ProxyProcessData proxyProcessData = new ProxyProcessData();

    protected ProxyProcessBuilder() {
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
     * Overrides the service id of the proxy that should be started.
     * The proxy will be identified by this in CloudNet, so care must be taken
     * to ensure that this property is never changed after the proxy has started.
     *
     * @param serviceId the new proxy id.
     *
     * @return the proxy process builder.
     */
    public ProxyProcessBuilder serviceId(ServiceId serviceId) {
        this.proxyProcessData.setServiceId(serviceId);
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
    public abstract CompletableFuture<ProxyProcessMeta> startProxy();

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

    protected ServiceId getServiceId() {
        return proxyProcessData.getServiceId();
    }

    public ProxyProcessData getProxyProcessData() {
        return proxyProcessData;
    }
}
