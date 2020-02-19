package de.dytanic.cloudnet.lib.process;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.*;

public class ProxyProcessData {

    public static final Type TYPE = TypeToken.get(ProxyProcessData.class).getType();

    private String wrapper;
    private String proxyGroupName;
    private int memory;
    private List<String> javaProcessParameters;
    private List<String> proxyProcessParameters;
    private String templateUrl;
    private Set<ServerInstallablePlugin> plugins;
    private Document properties;

    public ProxyProcessData() {
        this.javaProcessParameters = new ArrayList<>();
        this.proxyProcessParameters = new ArrayList<>();
        this.plugins = new HashSet<>();
        this.properties = new Document();
    }

    public ProxyProcessData(final String wrapper,
                            final String proxyGroupName,
                            final int memory,
                            final List<String> javaProcessParameters,
                            final List<String> proxyProcessParameters,
                            final String templateUrl,
                            final Set<ServerInstallablePlugin> plugins, final Document properties) {
        this.wrapper = wrapper;
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
        int result = wrapper != null ? wrapper.hashCode() : 0;
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
        if (!Objects.equals(wrapper, that.wrapper)) {
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
            "wrapper='" + wrapper + '\'' +
            ", proxyGroup=" + proxyGroupName +
            ", memory=" + memory +
            ", javaProcessParameters=" + javaProcessParameters +
            ", proxyProcessParameters=" + proxyProcessParameters +
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
