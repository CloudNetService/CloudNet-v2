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

import eu.cloudnetservice.cloudnet.v2.lib.server.ServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.server.priority.PriorityService;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.service.plugin.ServerInstallablePlugin;

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Builder for a server process.
 * Uses {@link ServerProcessData} for storing the data.
 */
public abstract class ServerProcessBuilder {
    private final ServerProcessData serverProcessData = new ServerProcessData();

    protected ServerProcessBuilder() {
    }


    /**
     * Sets the name of the server group.
     *
     * @param serverGroupName the name of the server group. Must not be null.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder serverGroupName(String serverGroupName) {
        Objects.requireNonNull(serverGroupName);
        this.serverProcessData.setServerGroupName(serverGroupName);
        return this;
    }

    /**
     * Sets the name of the wrapper that the server will be started on.
     * Set to {@code null} to not specify a wrapper.
     *
     * @param wrapperName the name of the wrapper to start the server on.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder wrapperName(String wrapperName) {
        this.serverProcessData.setWrapperName(wrapperName);
        return this;
    }

    /**
     * Sets the amount of memory for the heap of the server process in megabytes.
     *
     * @param memory the amount of memory the server process should get allocated in megabytes.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder memory(int memory) {
        this.serverProcessData.setMemory(memory);
        return this;
    }

    /**
     * Sets the parameters being passed to the Java runtime for starting the server process.
     *
     * @param javaProcessParameters a list containing all the parameters that will be passed onto the Java runtime. Must not be null.
     *
     * @return the server process builder.
     *
     * @see #serverProcessParameters(List)
     */
    public ServerProcessBuilder javaProcessParameters(List<String> javaProcessParameters) {
        this.serverProcessData.getJavaProcessParameters().clear();
        this.serverProcessData.getJavaProcessParameters().addAll(javaProcessParameters);
        return this;
    }

    /**
     * Sets the parameters being passed to the server executable.
     *
     * @param serverProcessParameters a list containing all the parameters that will be passed to the server executable. Must not be null.
     *
     * @return the server process builder.
     *
     * @see #javaProcessParameters(List)
     */
    public ServerProcessBuilder serverProcessParameters(List<String> serverProcessParameters) {
        this.serverProcessData.getServerProcessParameters().clear();
        this.serverProcessData.getServerProcessParameters().addAll(serverProcessParameters);
        return this;
    }

    /**
     * Sets the URL to download the template from.
     * Setting this to not {@code null} overrides the template of the server.
     *
     * @param templateUrl the url to download the template from.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder templateUrl(String templateUrl) {
        this.serverProcessData.setTemplateUrl(templateUrl);
        return this;
    }

    /**
     * Sets the plugins that will be installed to this server prior to it starting.
     *
     * @param plugins a set of plugins that will be installed.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder plugins(Set<ServerInstallablePlugin> plugins) {
        this.serverProcessData.getPlugins().clear();
        this.serverProcessData.getPlugins().addAll(plugins);
        return this;
    }

    /**
     * Sets the properties to override the default settings in the {@code server.properties} file.
     *
     * @param properties the overrides for the properties file.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder properties(Properties properties) {
        this.serverProcessData.setProperties(properties);
        return this;
    }

    /**
     * Sets the template that the server will be started from.
     * If this is set to {@code null}, then a random template will be chosen.
     *
     * @param template the template the server will start from.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder template(Template template) {
        this.serverProcessData.setTemplate(template);
        return this;
    }

    /**
     * Sets the server process' additional configuration regarding additional information
     * and when to start the server process.
     *
     * @param serverConfig additional process configuration.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder serverConfig(ServerConfig serverConfig) {
        this.serverProcessData.setServerConfig(serverConfig);
        return this;
    }

    /**
     * Adds a parameter for the Java runtime.
     *
     * @param parameter the parameter to add.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder addJavaProcessParameter(String parameter) {
        this.serverProcessData.getJavaProcessParameters().add(parameter);
        return this;
    }

    /**
     * Adds a parameter for the server executable.
     *
     * @param parameter the parameter to add.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder addProxyProcessParameter(String parameter) {
        this.serverProcessData.getServerProcessParameters().add(parameter);
        return this;
    }

    /**
     * Adds a plugin that will be installed prior to the server starting.
     *
     * @param plugin the plugin to install.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder addPlugin(ServerInstallablePlugin plugin) {
        this.serverProcessData.getPlugins().add(plugin);
        return this;
    }

    /**
     * Enables or disables the priority stop feature of CloudNet on servers started by this
     * server process builder.
     * When enabled, servers without players on them will automatically stop after the configured
     * amount of time in seconds of {@link PriorityService#getStopTimeInSeconds()} has passed.
     *
     * @param priorityStop whether the priority stop feature is active on this server.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder priorityStop(boolean priorityStop) {
        this.serverProcessData.setPriorityStop(priorityStop);
        return this;
    }

    /**
     * Overrides the service id of the server that should be started.
     * The server will be identified by this in CloudNet, so care must be taken
     * to ensure that this property is never changed after the server has started.
     *
     * @param serviceId the new server id.
     *
     * @return the server process builder.
     */
    public ServerProcessBuilder serviceId(ServiceId serviceId) {
        this.serverProcessData.setServiceId(serviceId);
        return this;
    }

    /**
     * Requests the master to initiate this server startup.
     * This will trigger a sequence of messages to be passed between the calling service,
     * the master and the wrapper that will start the server.
     * <p>
     * Once the server is started, the returned future will be completed and contains the
     * process metadata that was valid at the time of starting up.
     * <p>
     * Note that the delay between requesting the start (ie. this method) and the actual
     * completion of the returned future is indefinite and the future may not actually
     * be completed at all-
     *
     * @return a future that will be completed once the server is connected to the cloud network.
     */
    public abstract CompletableFuture<ServerProcessMeta> startServer();

    public String getWrapperName() {
        return serverProcessData.getWrapperName();
    }

    public String getServerGroupName() {
        return serverProcessData.getServerGroupName();
    }

    public int getMemory() {
        return serverProcessData.getMemory();
    }

    public List<String> getJavaProcessParameters() {
        return serverProcessData.getJavaProcessParameters();
    }

    public List<String> getProxyProcessParameters() {
        return serverProcessData.getServerProcessParameters();
    }

    public String getTemplateUrl() {
        return serverProcessData.getTemplateUrl();
    }

    public Set<ServerInstallablePlugin> getPlugins() {
        return serverProcessData.getPlugins();
    }

    public Properties getProperties() {
        return serverProcessData.getProperties();
    }

    public ServerConfig getServerConfig() {
        return serverProcessData.getServerConfig();
    }

    public Template getTemplate() {
        return serverProcessData.getTemplate();
    }

    public boolean isPriorityStop() {
        return this.serverProcessData.isPriorityStop();
    }

    public ServiceId getServiceId() {
        return this.serverProcessData.getServiceId();
    }

    public ServerProcessData getServerProcessData() {
        return serverProcessData;
    }

}
