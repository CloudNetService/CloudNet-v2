package de.dytanic.cloudnet.lib.process;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

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
     * Creates a new data holder for server process data with default values.
     * The server configuration will be set to not hide the server, an empty string for the extra,
     * an empty document for properties and immediate (ie. now) startup time.
     */
    public ServerProcessData() {
        this.serverConfig = new ServerConfig(false, "", new Document(), System.currentTimeMillis());
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
                             final Properties properties) {
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
}
