package de.dytanic.cloudnet.lib.process;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.*;

public class ServerProcessData {

    public static final Type TYPE = TypeToken.get(ServerProcessData.class).getType();

    private String wrapper;
    private String serverGroupName;
    private int memory;
    private ServerConfig serverConfig;
    private Template template;
    private List<String> javaProcessParameters;
    private List<String> serverProcessParameters;
    private String templateUrl;
    private Set<ServerInstallablePlugin> plugins;
    private Document properties;

    public ServerProcessData() {
        this.javaProcessParameters = new ArrayList<>();
        this.serverProcessParameters = new ArrayList<>();
        this.plugins = new HashSet<>();
        this.properties = new Document();
    }

    public ServerProcessData(final String wrapper,
                             final String serverGroupName,
                             final int memory,
                             final ServerConfig serverConfig,
                             final Template template,
                             final List<String> javaProcessParameters,
                             final List<String> serverProcessParameters,
                             final String templateUrl, final Set<ServerInstallablePlugin> plugins, final Document properties) {
        this.wrapper = wrapper;
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
        int result = wrapper != null ? wrapper.hashCode() : 0;
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
        if (!Objects.equals(wrapper, that.wrapper)) {
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
        return "ServerProcess{" +
            "wrapper='" + wrapper + '\'' +
            ", serverGroup=" + serverGroupName +
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

    public String getWrapper() {
        return wrapper;
    }

    public void setWrapper(final String wrapper) {
        this.wrapper = wrapper;
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

    public Document getProperties() {
        return properties;
    }

    public void setProperties(final Document properties) {
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
