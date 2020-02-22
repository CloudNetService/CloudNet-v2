package de.dytanic.cloudnet.lib.process;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Data class for storing and transferring information about a proxy process that is yet to be started.
 */
public class ProxyProcessData {

    public static final Type TYPE = TypeToken.get(ProxyProcessData.class).getType();

    /**
     * The name of the wrapper to start the server on.
     * {@code null}, if none is specified.
     */
    private String wrapperName;

    /**
     * The name of the proxy group to start the proxy from.
     */
    private String proxyGroupName;

    /**
     * The amount of memory the proxy will be started with.
     * This setting is done in megabytes.
     */
    private int memory;

    /**
     * A list of all parameters that will be passed to the Java process.
     */
    private List<String> javaProcessParameters;

    /**
     * A list of all parameters that will be passed to the proxy executable.
     */
    private List<String> proxyProcessParameters;

    /**
     * The URL of the template that will be used instead of the specified template.
     */
    private String templateUrl;

    /**
     * A set of plugins that will be installed on the proxy prior to starting it.
     */
    private Set<ServerInstallablePlugin> plugins;

    /**
     * Additional properties to store in the proxy process for custom usage.
     */
    private Document properties;

    public ProxyProcessData() {
        this.javaProcessParameters = new ArrayList<>();
        this.proxyProcessParameters = new ArrayList<>();
        this.plugins = new HashSet<>();
        this.properties = new Document();
    }

    public ProxyProcessData(final String wrapperName,
                            final String proxyGroupName,
                            final int memory,
                            final List<String> javaProcessParameters,
                            final List<String> proxyProcessParameters,
                            final String templateUrl,
                            final Set<ServerInstallablePlugin> plugins, final Document properties) {
        this.wrapperName = wrapperName;
        this.proxyGroupName = proxyGroupName;
        this.memory = memory;
        this.javaProcessParameters = javaProcessParameters;
        this.proxyProcessParameters = proxyProcessParameters;
        this.templateUrl = templateUrl;
        this.plugins = plugins;
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        int result = wrapperName != null ? wrapperName.hashCode() : 0;
        result = 31 * result + (proxyGroupName != null ? proxyGroupName.hashCode() : 0);
        result = 31 * result + memory;
        result = 31 * result + (javaProcessParameters != null ? javaProcessParameters.hashCode() : 0);
        result = 31 * result + (proxyProcessParameters != null ? proxyProcessParameters.hashCode() : 0);
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

        final ProxyProcessData that = (ProxyProcessData) o;

        if (memory != that.memory) {
            return false;
        }
        if (!Objects.equals(wrapperName, that.wrapperName)) {
            return false;
        }
        if (!Objects.equals(proxyGroupName, that.proxyGroupName)) {
            return false;
        }
        if (!Objects.equals(javaProcessParameters, that.javaProcessParameters)) {
            return false;
        }
        if (!Objects.equals(proxyProcessParameters, that.proxyProcessParameters)) {
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
        return "ProxyProcessData{" +
            "wrapper='" + wrapperName + '\'' +
            ", proxyGroup=" + proxyGroupName +
            ", memory=" + memory +
            ", javaProcessParameters=" + javaProcessParameters +
            ", proxyProcessParameters=" + proxyProcessParameters +
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

    public String getProxyGroupName() {
        return proxyGroupName;
    }

    public void setProxyGroupName(final String proxyGroupName) {
        this.proxyGroupName = proxyGroupName;
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

    public List<String> getProxyProcessParameters() {
        return proxyProcessParameters;
    }

    public void setProxyProcessParameters(final List<String> proxyProcessParameters) {
        this.proxyProcessParameters = proxyProcessParameters;
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

}
