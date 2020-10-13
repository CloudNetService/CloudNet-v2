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

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.priority.PriorityService;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.service.plugin.ServerInstallablePlugin;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Data class for storing and transferring information about a server process that is yet to be started.
 */
public class ServerProcessData {

    public static final Type TYPE = TypeToken.get(ServerProcessData.class).getType();

    /**
     * The name of the wrapper to start the server on.
     * {@code null}, if none is specified.
     */
    private String wrapperName;

    /**
     * The name of the server group to start the server from.
     */
    private String serverGroupName;

    /**
     * The amount of memory the server will be started with.
     * This setting is done in megabytes.
     */
    private int memory;

    /**
     * The server configuration the server will be started with.
     */
    private ServerConfig serverConfig;

    /**
     * The template the server will be started with.
     * {@code null}, if none is specified.
     */
    private Template template;

    /**
     * A list of all parameters that will be passed to the Java process.
     */
    private List<String> javaProcessParameters;

    /**
     * A list of all parameters that will be passed to the server executable.
     */
    private List<String> serverProcessParameters;

    /**
     * The URL of the template that will be used instead of the specified template.
     */
    private String templateUrl;

    /**
     * A set of plugins that will be installed on the server prior to starting it.
     */
    private Set<ServerInstallablePlugin> plugins;

    /**
     * Additional overrides for the {@code server.properties} file.
     */
    private Properties properties;

    /**
     * Determines whether the priority stop feature is active for this server process.
     * Stops the server after no players are on this server and the amount of time in
     * seconds of {@link PriorityService#getStopTimeInSeconds()} have passed.
     */
    private boolean priorityStop;

    /**
     * The service id of this server.
     * The server is identified using this property so care must be taken to ensure
     * that this object is not mutated after the server has started.
     */
    private ServiceId serviceId;

    /**
     * Creates a new data holder for server process data with default values.
     * The server configuration will be set to not hide the server, an empty string for the extra,
     * an empty document for properties and immediate (ie. now) startup time.
     */
    public ServerProcessData() {
        this.serverConfig = new ServerConfig();
        this.javaProcessParameters = new ArrayList<>();
        this.serverProcessParameters = new ArrayList<>();
        this.plugins = new HashSet<>();
        this.properties = new Properties();
    }

    /**
     * Creates a new data holder for server process data.
     *
     * @param wrapperName             the wrapper id of the wrapper to start the server on.
     * @param serverGroupName         the name of the server group.
     * @param memory                  the amount of memory in megabytes.
     * @param serverConfig            the server configuration.
     * @param template                the template the server will be started with.
     * @param javaProcessParameters   parameters for the Java process.
     * @param serverProcessParameters parameters for the server process.
     * @param templateUrl             the url of the template to download.
     * @param plugins                 the set of plugins that will be downloaded prior to starting the server.
     * @param properties              modifications done to the {@code server.properties} file.
     */
    public ServerProcessData(final String wrapperName,
                             final String serverGroupName,
                             final int memory,
                             final ServerConfig serverConfig,
                             final Template template,
                             final List<String> javaProcessParameters,
                             final List<String> serverProcessParameters,
                             final String templateUrl,
                             final Set<ServerInstallablePlugin> plugins,
                             final Properties properties, final ServiceId serviceId) {
        this.wrapperName = wrapperName;
        this.serverGroupName = serverGroupName;
        this.memory = memory;
        this.serverConfig = serverConfig;
        this.template = template;
        this.javaProcessParameters = javaProcessParameters;
        this.serverProcessParameters = serverProcessParameters;
        this.templateUrl = templateUrl;
        this.plugins = plugins;
        this.properties = properties;
        this.serviceId = serviceId;
    }

    public ServerProcessData(final ServerProcessData serverProcessData, final ServiceId serviceId) {
        this.wrapperName = serverProcessData.wrapperName;
        this.serverGroupName = serverProcessData.serverGroupName;
        this.serverConfig = serverProcessData.serverConfig;
        this.template = serverProcessData.template;
        this.memory = serverProcessData.memory;
        this.javaProcessParameters = serverProcessData.javaProcessParameters;
        this.serverProcessParameters = serverProcessData.serverProcessParameters;
        this.templateUrl = serverProcessData.templateUrl;
        this.plugins = serverProcessData.plugins;
        this.properties = serverProcessData.properties;
        this.serviceId = serviceId;
    }

    @Override
    public int hashCode() {
        int result = wrapperName != null ? wrapperName.hashCode() : 0;
        result = 31 * result + (serverGroupName != null ? serverGroupName.hashCode() : 0);
        result = 31 * result + memory;
        result = 31 * result + (serverConfig != null ? serverConfig.hashCode() : 0);
        result = 31 * result + (template != null ? template.hashCode() : 0);
        result = 31 * result + (javaProcessParameters != null ? javaProcessParameters.hashCode() : 0);
        result = 31 * result + (serverProcessParameters != null ? serverProcessParameters.hashCode() : 0);
        result = 31 * result + (templateUrl != null ? templateUrl.hashCode() : 0);
        result = 31 * result + (plugins != null ? plugins.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
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

        final ServerProcessData that = (ServerProcessData) o;

        if (memory != that.memory) {
            return false;
        }
        if (!Objects.equals(wrapperName, that.wrapperName)) {
            return false;
        }
        if (!Objects.equals(serverGroupName, that.serverGroupName)) {
            return false;
        }
        if (!Objects.equals(serverConfig, that.serverConfig)) {
            return false;
        }
        if (!Objects.equals(template, that.template)) {
            return false;
        }
        if (!Objects.equals(javaProcessParameters, that.javaProcessParameters)) {
            return false;
        }
        if (!Objects.equals(serverProcessParameters, that.serverProcessParameters)) {
            return false;
        }
        if (!Objects.equals(templateUrl, that.templateUrl)) {
            return false;
        }
        if (!Objects.equals(plugins, that.plugins)) {
            return false;
        }
        return Objects.equals(properties, that.properties);
    }

    @Override
    public String toString() {
        return "ServerProcessData{" +
            "wrapperName='" + wrapperName + '\'' +
            ", serverGroupName='" + serverGroupName + '\'' +
            ", memory=" + memory +
            ", serverConfig=" + serverConfig +
            ", template=" + template +
            ", javaProcessParameters=" + javaProcessParameters +
            ", serverProcessParameters=" + serverProcessParameters +
            ", templateUrl='" + templateUrl + '\'' +
            ", plugins=" + plugins +
            ", properties=" + properties +
            '}';
    }

    public String getWrapperName() {
        return wrapperName;
    }

    public void setWrapperName(final String wrapperName) {
        this.wrapperName = wrapperName;
    }

    public String getServerGroupName() {
        return serverGroupName;
    }

    public void setServerGroupName(final String serverGroupName) {
        this.serverGroupName = serverGroupName;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(final int memory) {
        this.memory = memory;
    }

    public List<String> getJavaProcessParameters() {
        return javaProcessParameters;
    }

    public void setJavaProcessParameters(final List<String> javaProcessParameters) {
        this.javaProcessParameters = javaProcessParameters;
    }

    public List<String> getServerProcessParameters() {
        return serverProcessParameters;
    }

    public void setServerProcessParameters(final List<String> serverProcessParameters) {
        this.serverProcessParameters = serverProcessParameters;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(final String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public Set<ServerInstallablePlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(final Set<ServerInstallablePlugin> plugins) {
        this.plugins = plugins;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(final ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(final Template template) {
        this.template = template;
    }

    public boolean isPriorityStop() {
        return priorityStop;
    }

    public void setPriorityStop(final boolean priorityStop) {
        this.priorityStop = priorityStop;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public void setServiceId(final ServiceId serviceId) {
        this.serviceId = serviceId;
    }
}
